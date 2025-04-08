package com.quafka.monitoring.impl;

import com.quafka.connection.ConnectionState;
import com.quafka.monitoring.ConnectionMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Varsayılan bağlantı izleme implementasyonu.
 */
public class DefaultConnectionMonitor implements ConnectionMonitor {
    private static final Logger logger = LoggerFactory.getLogger(DefaultConnectionMonitor.class);
    
    @Override
    public void monitorState(ConnectionState state) {
        logger.info("Bağlantı durumu değişti: {}", state);
    }
    
    @Override
    public void monitorError(String error) {
        logger.error("Bağlantı hatası: {}", error);
    }
    
    @Override
    public void monitorPerformance(long operationTime) {
        logger.debug("İşlem süresi: {} ns", operationTime);
    }
} 