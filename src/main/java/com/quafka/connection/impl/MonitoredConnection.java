package com.quafka.connection.impl;

import com.quafka.connection.Connection;
import com.quafka.connection.ConnectionException;
import com.quafka.connection.ConnectionState;
import com.quafka.config.ConnectionConfig;
import com.quafka.monitoring.ConnectionMetrics;
import com.quafka.monitoring.ConnectionMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * İzlenen bağlantı sınıfı.
 * Bağlantı izleme ve metrik toplama işlemlerini yönetir.
 */
public class MonitoredConnection implements Connection {
    private static final Logger logger = LoggerFactory.getLogger(MonitoredConnection.class);
    
    private final Connection delegate;
    private final ConnectionMonitor monitor;
    private final ConnectionMetrics metrics;
    
    public MonitoredConnection(Connection delegate, ConnectionMonitor monitor, ConnectionMetrics metrics) {
        this.delegate = delegate;
        this.monitor = monitor;
        this.metrics = metrics;
    }
    
    @Override
    public void connect() throws ConnectionException {
        try {
            delegate.connect();
            monitor.monitorState(ConnectionState.CONNECTED);
            metrics.recordConnection();
            logger.info("Bağlantı başarılı: {}", getConfig().getConnectionId());
        } catch (ConnectionException e) {
            monitor.monitorError("Bağlantı hatası: " + e.getMessage());
            metrics.recordError();
            throw e;
        }
    }
    
    @Override
    public void disconnect() throws ConnectionException {
        try {
            delegate.disconnect();
            monitor.monitorState(ConnectionState.DISCONNECTED);
            metrics.recordDisconnection();
            logger.info("Bağlantı kapatıldı: {}", getConfig().getConnectionId());
        } catch (ConnectionException e) {
            monitor.monitorError("Bağlantı kapatma hatası: " + e.getMessage());
            metrics.recordError();
            throw e;
        }
    }
    
    @Override
    public <T> T execute(Supplier<T> operation) throws ConnectionException {
        long startTime = System.nanoTime();
        try {
            T result = delegate.execute(operation);
            long duration = System.nanoTime() - startTime;
            monitor.monitorPerformance(duration);
            metrics.recordOperation(duration);
            return result;
        } catch (ConnectionException e) {
            monitor.monitorError("İşlem hatası: " + e.getMessage());
            metrics.recordError();
            throw e;
        }
    }
    
    @Override
    public <T> CompletableFuture<T> executeAsync(Supplier<T> operation) {
        return delegate.executeAsync(operation)
            .whenComplete((result, error) -> {
                if (error != null) {
                    monitor.monitorError("Asenkron işlem hatası: " + error.getMessage());
                    metrics.recordError();
                }
            });
    }

    @Override
    public String sendMessage(String message) throws ConnectionException {
        try {
            return delegate.sendMessage(message);
        } catch (ConnectionException e) {
            monitor.monitorError("Mesaj gönderme hatası: " + e.getMessage());
            metrics.recordError();
            throw e;
        }
    }

    @Override
    public CompletableFuture<String> sendMessageAsync(String message) {
        return delegate.sendMessageAsync(message)
            .whenComplete((result, error) -> {
                if (error != null) {
                    monitor.monitorError("Asenkron mesaj gönderme hatası: " + error.getMessage());
                    metrics.recordError();
                }
            });
    }

    @Override
    public ConnectionState getState() {
        return delegate.getState();
    }
    
    @Override
    public ConnectionConfig getConfig() {
        return delegate.getConfig();
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
        return delegate.isConnected();
    }
    
    @Override
    public void close() throws Exception {
        delegate.close();
    }
} 