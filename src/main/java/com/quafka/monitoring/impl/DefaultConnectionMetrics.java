package com.quafka.monitoring.impl;

import com.quafka.monitoring.ConnectionMetrics;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;

/**
 * Varsayılan bağlantı metrikleri implementasyonu.
 */
public class DefaultConnectionMetrics implements ConnectionMetrics {
    private final AtomicLong totalConnections = new AtomicLong(0);
    private final AtomicLong totalErrors = new AtomicLong(0);
    private final DoubleAdder totalOperationTime = new DoubleAdder();
    private final AtomicLong operationCount = new AtomicLong(0);
    private final AtomicLong messageProcessedCount = new AtomicLong(0);
    
    @Override
    public void recordConnection() {
        totalConnections.incrementAndGet();
    }
    
    @Override
    public void recordDisconnection() {
        // Bağlantı kapatma metrikleri burada toplanabilir
    }
    
    @Override
    public void recordOperation(long duration) {
        totalOperationTime.add(duration);
        operationCount.incrementAndGet();
    }
    
    @Override
    public void recordError() {
        totalErrors.incrementAndGet();
    }
    
    @Override
    public long getTotalConnections() {
        return totalConnections.get();
    }
    
    @Override
    public long getTotalErrors() {
        return totalErrors.get();
    }
    
    @Override
    public double getAverageOperationTime() {
        long count = operationCount.get();
        if (count == 0) {
            return 0.0;
        }
        return totalOperationTime.sum() / count;
    }

    public void recordMessageProcessed() {
        messageProcessedCount.incrementAndGet();
    }
} 