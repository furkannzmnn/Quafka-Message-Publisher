package com.quafka.reconnect.impl;

import com.quafka.connection.Connection;
import com.quafka.exception.ConnectionErrorCode;
import com.quafka.exception.ConnectionException;
import com.quafka.reconnect.ConnectionReconnectStrategy;

import java.util.concurrent.TimeUnit;

/**
 * Üstel geri çekilme (exponential backoff) stratejisi ile yeniden bağlanma uygulaması.
 */
public class ExponentialBackoffReconnectStrategy implements ConnectionReconnectStrategy {
    
    private static final int DEFAULT_MAX_ATTEMPTS = 5;
    private static final long DEFAULT_INITIAL_BACKOFF = TimeUnit.SECONDS.toMillis(1);
    private static final long DEFAULT_MAX_BACKOFF = TimeUnit.SECONDS.toMillis(30);
    
    private final int maxAttempts;
    private final long initialBackoff;
    private final long maxBackoff;
    
    public ExponentialBackoffReconnectStrategy() {
        this(DEFAULT_MAX_ATTEMPTS, DEFAULT_INITIAL_BACKOFF, DEFAULT_MAX_BACKOFF);
    }
    
    public ExponentialBackoffReconnectStrategy(int maxAttempts, long initialBackoff, long maxBackoff) {
        this.maxAttempts = maxAttempts;
        this.initialBackoff = initialBackoff;
        this.maxBackoff = maxBackoff;
    }
    
    @Override
    public boolean attemptReconnect(Connection connection) throws ConnectionException {
        if (connection == null) {
            return false;
        }
        
        try {
            connection.connect();
            return true;
        } catch (Exception e) {
            throw new ConnectionException(ConnectionErrorCode.UNKNOWN_ERROR,
                "Yeniden bağlanma sırasında hata oluştu", e);
        }
    }
    
    @Override
    public long getBackoffTime(int attempt) {
        if (attempt <= 0) {
            return 0;
        }
        
        long backoff = initialBackoff * (1L << (attempt - 1));
        return Math.min(backoff, maxBackoff);
    }
    
    @Override
    public int getMaxAttempts() {
        return maxAttempts;
    }
} 