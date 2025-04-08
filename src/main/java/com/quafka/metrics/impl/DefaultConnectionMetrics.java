package com.quafka.metrics.impl;

import com.quafka.metrics.ConnectionMetrics;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * Varsayılan bağlantı metrikleri uygulaması.
 */
public class DefaultConnectionMetrics implements ConnectionMetrics {
    
    private final LongAdder connectionCreationTime = new LongAdder();
    private final LongAdder connectionCloseTime = new LongAdder();
    private final LongAdder errorCount = new LongAdder();
    private final LongAdder successCount = new LongAdder();
    private final AtomicLong activeConnections = new AtomicLong();
    private final AtomicLong idleConnections = new AtomicLong();
    
    @Override
    public void recordConnectionCreation(long durationMs) {
        connectionCreationTime.add(durationMs);
    }
    
    @Override
    public void recordConnectionClose(long durationMs) {
        connectionCloseTime.add(durationMs);
    }
    
    @Override
    public void incrementErrorCount() {
        errorCount.increment();
    }
    
    @Override
    public void incrementSuccessCount() {
        successCount.increment();
    }
    
    @Override
    public void updatePoolMetrics(int activeConnections, int idleConnections) {
        this.activeConnections.set(activeConnections);
        this.idleConnections.set(idleConnections);
    }
    
    public long getTotalConnectionCreationTime() {
        return connectionCreationTime.sum();
    }
    
    public long getTotalConnectionCloseTime() {
        return connectionCloseTime.sum();
    }
    
    public long getErrorCount() {
        return errorCount.sum();
    }
    
    public long getSuccessCount() {
        return successCount.sum();
    }
    
    public long getActiveConnections() {
        return activeConnections.get();
    }
    
    public long getIdleConnections() {
        return idleConnections.get();
    }
} 