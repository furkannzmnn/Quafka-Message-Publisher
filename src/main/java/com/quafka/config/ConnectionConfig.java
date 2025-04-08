package com.quafka.config;

import com.quafka.loadbalancer.LoadBalancingStrategy;
import java.util.Objects;
import java.util.UUID;

/**
 * Bağlantı yapılandırmasını temsil eden sınıf.
 */
public class ConnectionConfig {
    private final String connectionId;
    private final String host;
    private final int port;
    private final int timeout;
    private final int maxRetries;
    private final long initialRetryDelay;
    private final long maxRetryDelay;
    private final LoadBalancingStrategy strategy;
    
    private ConnectionConfig(Builder builder) {
        this.connectionId = builder.connectionId;
        this.host = builder.host;
        this.port = builder.port;
        this.timeout = builder.timeout;
        this.maxRetries = builder.maxRetries;
        this.initialRetryDelay = builder.initialRetryDelay;
        this.maxRetryDelay = builder.maxRetryDelay;
        this.strategy = builder.strategy;
    }
    
    public String getConnectionId() {
        return connectionId;
    }
    
    public String getHost() {
        return host;
    }
    
    public int getPort() {
        return port;
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    public int getMaxRetries() {
        return maxRetries;
    }
    
    public long getInitialRetryDelay() {
        return initialRetryDelay;
    }
    
    public long getMaxRetryDelay() {
        return maxRetryDelay;
    }
    
    public LoadBalancingStrategy getStrategy() {
        return strategy;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionConfig that = (ConnectionConfig) o;
        return port == that.port &&
            timeout == that.timeout &&
            maxRetries == that.maxRetries &&
            initialRetryDelay == that.initialRetryDelay &&
            maxRetryDelay == that.maxRetryDelay &&
            Objects.equals(connectionId, that.connectionId) &&
            Objects.equals(host, that.host) &&
            strategy == that.strategy;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(connectionId, host, port, timeout, maxRetries,
            initialRetryDelay, maxRetryDelay, strategy);
    }
    
    /**
     * ConnectionConfig için builder sınıfı.
     */
    public static class Builder {
        private String connectionId = UUID.randomUUID().toString();
        private String host = "localhost";
        private int port = 8080;
        private int timeout = 5000;
        private int maxRetries = 3;
        private long initialRetryDelay = 1000;
        private long maxRetryDelay = 10000;
        private LoadBalancingStrategy strategy = LoadBalancingStrategy.ROUND_ROBIN;
        
        public Builder withConnectionId(String connectionId) {
            this.connectionId = connectionId;
            return this;
        }
        
        public Builder withHost(String host) {
            this.host = host;
            return this;
        }
        
        public Builder withPort(int port) {
            this.port = port;
            return this;
        }
        
        public Builder withTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }
        
        public Builder withMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }
        
        public Builder withInitialRetryDelay(long initialRetryDelay) {
            this.initialRetryDelay = initialRetryDelay;
            return this;
        }
        
        public Builder withMaxRetryDelay(long maxRetryDelay) {
            this.maxRetryDelay = maxRetryDelay;
            return this;
        }
        
        public Builder withStrategy(LoadBalancingStrategy strategy) {
            this.strategy = strategy;
            return this;
        }
        
        public ConnectionConfig build() {
            return new ConnectionConfig(this);
        }
    }
} 