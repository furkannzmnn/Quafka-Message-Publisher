package com.quafka.server.impl;

import com.quafka.config.ServerConfig;
import com.quafka.server.Server;
import com.quafka.topic.TopicManager;
import com.quafka.topic.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Varsayılan sunucu implementasyonu.
 */
public class DefaultServer implements Server {
    private static final Logger logger = LoggerFactory.getLogger(DefaultServer.class);
    
    private final ServerConfig config;
    private final AtomicBoolean running;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    
    public DefaultServer(ServerConfig config) {
        this.config = config;
        this.running = new AtomicBoolean(false);
    }
    
    @Override
    public void start() throws Exception {
        if (running.compareAndSet(false, true)) {
            logger.info("Sunucu başlatılıyor: port={}", config.getPort());
            
            serverSocket = new ServerSocket(config.getPort(), config.getBacklog());
            executorService = Executors.newFixedThreadPool(config.getWorkerThreads());
            
            // Bağlantı kabul etme döngüsü
            executorService.submit(() -> {
                while (running.get()) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        logger.info("Yeni bağlantı kabul edildi: {}", clientSocket.getRemoteSocketAddress());
                        
                        executorService.submit(() -> handleClient(clientSocket));
                    } catch (IOException e) {
                        if (running.get()) {
                            logger.error("Bağlantı kabul edilirken hata oluştu", e);
                        }
                    }
                }
            });
            
            logger.info("Sunucu başlatıldı: port={}", config.getPort());
        }
    }
    
    @Override
    public void stop() throws Exception {
        if (running.compareAndSet(true, false)) {
            logger.info("Sunucu durduruluyor");
            
            if (serverSocket != null) {
                serverSocket.close();
            }
            
            if (executorService != null) {
                executorService.shutdown();
            }
            
            logger.info("Sunucu durduruldu");
        }
    }
    
    @Override
    public ServerConfig getConfig() {
        return config;
    }
    
    @Override
    public boolean isRunning() {
        return running.get();
    }
    
    @Override
    public void close() throws Exception {
        stop();
    }
    
    private void handleClient(Socket clientSocket) {
        try (Socket socket = clientSocket) {
            var reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            );
            var writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
            );
            
            String message;
            while ((message = reader.readLine()) != null) {
                logger.info("Gelen mesaj: {}", message);
                
                String response = processMessage(message);
                writer.write(response);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            logger.error("İstemci işlenirken hata oluştu", e);
        }
    }
    
    private String processMessage(String message) {
        try {
            if (message == null || message.trim().isEmpty()) {
                return "HATA: Boş mesaj";
            }

            // Mesaj formatını kontrol et
            String[] parts = message.split(":", 2);
            if (parts.length != 2) {
                return "HATA: Geçersiz mesaj formatı";
            }

            String command = parts[0];
            String content = parts[1];

            // Komuta göre işlem yap
            switch (command) {
                case "TEXT":
                    // Mesajı topic'e ekle
                    TopicManager.PartitionOffset offset = TopicManager.getInstance()
                        .publish("default-topic", content);
                    return String.format("OK: Mesaj topic'e eklendi (partition: %d, offset: %d)", 
                        offset.getPartitionId(), offset.getOffset());
                case "POLL":
                    // Topic'ten mesaj al
                    String[] pollParts = content.split(":");
                    if (pollParts.length != 2) {
                        return "HATA: Geçersiz POLL formatı (topic:partition)";
                    }
                    
                    String topic = pollParts[0];
                    int partitionId = Integer.parseInt(pollParts[1]);
                    
                    TopicPartition.MessageWithOffset polledMessage = TopicManager.getInstance()
                        .poll(topic, partitionId, -1);
                    
                    return polledMessage != null ? 
                        String.format("OK: %s (offset: %d)", 
                            polledMessage.getMessage(), polledMessage.getOffset()) : 
                        "OK: Mesaj bulunamadı";
                case "COMMIT":
                    return "OK: " + content + " için commit başarılı";
                default:
                    return "HATA: Bilinmeyen komut: " + command;
            }
        } catch (Exception e) {
            logger.error("Mesaj işlenirken hata oluştu", e);
            return "HATA: " + e.getMessage();
        }
    }
} 