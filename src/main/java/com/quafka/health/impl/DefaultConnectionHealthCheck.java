package com.quafka.health.impl;

import com.quafka.connection.Connection;
import com.quafka.health.ConnectionHealthCheck;

import java.util.concurrent.TimeUnit;

/**
 * Varsayılan bağlantı sağlık kontrolü uygulaması.
 */
public class DefaultConnectionHealthCheck implements ConnectionHealthCheck {
    
    private static final long DEFAULT_CHECK_INTERVAL = TimeUnit.SECONDS.toMillis(30);
    private static final long DEFAULT_TIMEOUT = TimeUnit.SECONDS.toMillis(5);
    
    private final long checkInterval;
    private final long timeout;
    
    public DefaultConnectionHealthCheck() {
        this(DEFAULT_CHECK_INTERVAL, DEFAULT_TIMEOUT);
    }
    
    public DefaultConnectionHealthCheck(long checkInterval, long timeout) {
        this.checkInterval = checkInterval;
        this.timeout = timeout;
    }
    
    @Override
    public boolean isHealthy(Connection connection) {
        if (connection == null) {
            return false;
        }
        
        try {
            return connection.isConnected();
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public long getCheckInterval() {
        return checkInterval;
    }
    
    @Override
    public long getTimeout() {
        return timeout;
    }
} 