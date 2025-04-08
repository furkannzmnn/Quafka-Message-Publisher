package com.quafka.connection.impl;

import com.quafka.connection.Connection;
import com.quafka.connection.ConnectionException;
import com.quafka.connection.ConnectionState;
import com.quafka.config.ConnectionConfig;
import com.quafka.monitoring.ConnectionMetrics;
import com.quafka.monitoring.ConnectionMonitor;
import com.quafka.monitoring.impl.DefaultConnectionMonitor;
import com.quafka.monitoring.impl.DefaultConnectionMetrics;
import com.quafka.retry.RetryPolicy;
import com.quafka.retry.impl.ExponentialBackoffRetryPolicy;
import com.quafka.topic.TopicManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Varsayılan bağlantı implementasyonu.
 * TCP Socket üzerinden mesajlaşma sağlar.
 */
public class DefaultConnection implements Connection {
    private static final Logger logger = LoggerFactory.getLogger(DefaultConnection.class);
    
    private final ConnectionConfig config;
    private final AtomicReference<ConnectionState> state;
    private final ConnectionMonitor monitor;
    private final ConnectionMetrics metrics;
    private final RetryPolicy retryPolicy;
    
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    
    public DefaultConnection(ConnectionConfig config) {
        this.config = config;
        this.state = new AtomicReference<>(ConnectionState.DISCONNECTED);
        this.monitor = new DefaultConnectionMonitor();
        this.metrics = new DefaultConnectionMetrics();
        this.retryPolicy = new ExponentialBackoffRetryPolicy(
            config.getMaxRetries(),
            config.getInitialRetryDelay(),
            config.getMaxRetryDelay()
        );
    }
    
    @Override
    public void connect() throws ConnectionException {
        if (state.compareAndSet(ConnectionState.DISCONNECTED, ConnectionState.CONNECTING)) {
            try {
                logger.info("Bağlantı kuruluyor: {}", config.getConnectionId());
                
                // Socket bağlantısını oluştur
                socket = new Socket(config.getHost(), config.getPort());
                socket.setSoTimeout(config.getTimeout());
                
                // I/O akışlarını oluştur
                reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
                );
                writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
                );
                
                state.set(ConnectionState.CONNECTED);
                metrics.recordConnection();
                monitor.monitorState(ConnectionState.CONNECTED);
                logger.info("Bağlantı başarılı: {}", config.getConnectionId());
            } catch (Exception e) {
                closeQuietly();
                state.set(ConnectionState.DISCONNECTED);
                metrics.recordError();
                monitor.monitorError("Bağlantı hatası: " + e.getMessage());
                throw new ConnectionException("Bağlantı kurulamadı: " + e.getMessage(), e);
            }
        }
    }
    
    @Override
    public void disconnect() throws ConnectionException {
        if (state.compareAndSet(ConnectionState.CONNECTED, ConnectionState.DISCONNECTING)) {
            try {
                logger.info("Bağlantı kapatılıyor: {}", config.getConnectionId());
                closeQuietly();
                state.set(ConnectionState.DISCONNECTED);
                metrics.recordDisconnection();
                monitor.monitorState(ConnectionState.DISCONNECTED);
                logger.info("Bağlantı kapatıldı: {}", config.getConnectionId());
            } catch (Exception e) {
                state.set(ConnectionState.DISCONNECTED);
                metrics.recordError();
                monitor.monitorError("Bağlantı kapatma hatası: " + e.getMessage());
                throw new ConnectionException("Bağlantı kapatılamadı: " + e.getMessage(), e);
            }
        }
    }
    
    @Override
    public <T> T execute(Supplier<T> operation) throws ConnectionException {
        if (state.get() != ConnectionState.CONNECTED) {
            throw new ConnectionException("Bağlantı aktif değil");
        }
        
        try {
            return retryPolicy.execute(() -> {
                long startTime = System.nanoTime();
                T result = operation.get();
                long duration = System.nanoTime() - startTime;
                
                metrics.recordOperation(duration);
                monitor.monitorPerformance(duration);
                return result;
            });
        } catch (Exception e) {
            metrics.recordError();
            monitor.monitorError("İşlem hatası: " + e.getMessage());
            throw new ConnectionException("İşlem başarısız oldu: " + e.getMessage(), e);
        }
    }
    
    /**
     * Mesaj gönderir ve yanıtı bekler.
     * @param message Gönderilecek mesaj
     * @return Alınan yanıt
     * @throws ConnectionException Mesaj gönderilemezse veya yanıt alınamazsa
     */
    @Override
    public String sendMessage(String message) throws ConnectionException {
        if (!isConnected()) {
            throw new ConnectionException("Bağlantı kapalı");
        }

        try {
            // Mesajı gönder
            writer.write(message);
            writer.newLine();
            writer.flush();

            // Yanıtı bekle
            StringBuilder response = new StringBuilder();
            int ch;
            while ((ch = reader.read()) != '\n' && ch != -1) {
                response.append((char) ch);
            }

            String responseStr = response.toString();

            if (message.startsWith("TEXT:")) {
                String content = message.substring(5);
                TopicManager.getInstance().publish("default-topic", content);
            }

            return responseStr;
        } catch (IOException e) {
            throw new ConnectionException("Mesaj gönderilemedi: " + e.getMessage(), e);
        }
    }
    
    /**
     * Mesajı asenkron olarak gönderir.
     * @param message Gönderilecek mesaj
     * @return Yanıt için CompletableFuture
     */
    @Override
    public CompletableFuture<String> sendMessageAsync(String message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sendMessage(message);
            } catch (ConnectionException e) {
                throw new CompletionException(e);
            }
        });
    }
    
    private void closeQuietly() {
        try {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            logger.warn("Bağlantı kapatılırken hata oluştu", e);
        } finally {
            writer = null;
            reader = null;
            socket = null;
        }
    }
    
    @Override
    public <T> CompletableFuture<T> executeAsync(Supplier<T> operation) {
        if (state.get() != ConnectionState.CONNECTED) {
            return CompletableFuture.failedFuture(
                new ConnectionException("Bağlantı aktif değil")
            );
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                return execute(operation);
            } catch (ConnectionException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    @Override
    public ConnectionState getState() {
        return state.get();
    }
    
    @Override
    public ConnectionConfig getConfig() {
        return config;
    }
    
    @Override
    public ConnectionMonitor getMonitor() {
        return monitor;
    }
    
    @Override
    public ConnectionMetrics getMetrics() {
        return metrics;
    }
    
    @Override
    public boolean isConnected() {
        return state.get() == ConnectionState.CONNECTED;
    }
    
    @Override
    public void close() throws Exception {
        if (isConnected()) {
            disconnect();
        }
    }
} 