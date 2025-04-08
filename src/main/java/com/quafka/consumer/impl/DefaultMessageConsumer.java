package com.quafka.consumer.impl;

import com.quafka.config.ConnectionConfig;
import com.quafka.config.ConsumerConfig;
import com.quafka.consumer.MessageConsumer;
import com.quafka.connection.Connection;
import com.quafka.connection.impl.DefaultConnection;
import com.quafka.monitoring.impl.DefaultConnectionMonitor;
import com.quafka.monitoring.impl.DefaultConnectionMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Varsayılan mesaj tüketici implementasyonu.
 */
public class DefaultMessageConsumer implements MessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(DefaultMessageConsumer.class);
    
    private final ConsumerConfig config;
    private final AtomicBoolean running;
    private final ExecutorService executorService;
    private Connection connection;
    private DefaultConnectionMetrics metrics;
    
    public DefaultMessageConsumer(ConsumerConfig config) {
        this.config = config;
        this.running = new AtomicBoolean(false);
        this.executorService = Executors.newSingleThreadExecutor();
    }
    
    @Override
    public void start() throws Exception {
        if (running.compareAndSet(false, true)) {
            logger.info("Tüketici başlatılıyor: groupId={}, topic={}", 
                config.getGroupId(), config.getTopic());
            
            ConnectionConfig connectionConfig = new ConnectionConfig.Builder()
                .withHost("localhost")
                .withPort(8080)
                .withTimeout((int) config.getPollTimeout())
                .build();
            
            connection = new DefaultConnection(connectionConfig);
            connection.connect();
            
            executorService.submit(this::consumeMessages);
            
            logger.info("Tüketici başlatıldı: groupId={}, topic={}", 
                config.getGroupId(), config.getTopic());
        }
    }
    
    @Override
    public void stop() throws Exception {
        if (running.compareAndSet(true, false)) {
            logger.info("Tüketici durduruluyor");
            
            if (connection != null) {
                connection.close();
            }
            
            executorService.shutdown();
            
            logger.info("Tüketici durduruldu");
        }
    }
    
    @Override
    public ConsumerConfig getConfig() {
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
    
    private void consumeMessages() {
        while (running.get()) {
            try {
                // Topic'ten mesaj al
                String message = connection.sendMessage("POLL:" + config.getTopic());
                logger.info("Mesaj alındı: {}", message);
                
                // Mesajı işle
                processMessage(message);
                
                // Auto-commit aktifse commit yap
                if (config.isAutoCommit()) {
                    connection.sendMessage("COMMIT:" + config.getGroupId());
                }
                
                // Belirtilen aralık kadar bekle
                Thread.sleep(config.getPollInterval());
            } catch (InterruptedException e) {
                if (running.get()) {
                    logger.warn("Tüketici kesintiye uğradı", e);
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                if (running.get()) {
                    logger.error("Mesaj tüketilirken hata oluştu", e);
                    // Hata durumunda kısa bir bekleme süresi
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }
    
    private void processMessage(String message) {
        try {
            if (message == null || message.trim().isEmpty()) {
                logger.warn("Boş mesaj alındı");
                return;
            }

            String[] parts = message.split(":", 2);
            if (parts.length != 2) {
                logger.warn("Geçersiz mesaj formatı: {}", message);
                return;
            }

            String messageType = parts[0];
            String messageContent = parts[1];

            switch (messageType) {
                case "TEXT":
                    handleTextMessage(messageContent);
                    break;
                case "JSON":
                    handleJsonMessage(messageContent);
                    break;
                case "BINARY":
                    handleBinaryMessage(messageContent);
                    break;
                default:
                    logger.warn("Bilinmeyen mesaj tipi: {}", messageType);
            }

            logger.info("Mesaj başarıyla işlendi: type={}, content={}", messageType, messageContent);
        } catch (Exception e) {
            logger.error("Mesaj işlenirken hata oluştu: {}", message, e);
            metrics.recordError();
        }
    }
    
    private void handleTextMessage(String content) {
        logger.debug("Metin mesajı işleniyor: {}", content);
    }
    
    private void handleJsonMessage(String content) {
        logger.debug("JSON mesajı işleniyor: {}", content);
    }
    
    private void handleBinaryMessage(String content) {
        logger.debug("Binary mesaj işleniyor: {}", content);
    }
} 