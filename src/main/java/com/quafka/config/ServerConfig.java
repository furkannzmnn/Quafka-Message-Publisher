package com.quafka.config;

import java.util.Objects;

/**
 * Sunucu yapılandırmasını temsil eden sınıf.
 */
public class ServerConfig {
    private final int port;
    private final int backlog;
    private final int maxConnections;
    private final int workerThreads;
    
    private ServerConfig(Builder builder) {
        this.port = builder.port;
        this.backlog = builder.backlog;
        this.maxConnections = builder.maxConnections;
        this.workerThreads = builder.workerThreads;
    }
    
    public int getPort() {
        return port;
    }
    
    public int getBacklog() {
        return backlog;
    }
    
    public int getMaxConnections() {
        return maxConnections;
    }
    
    public int getWorkerThreads() {
        return workerThreads;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerConfig that = (ServerConfig) o;
        return port == that.port &&
            backlog == that.backlog &&
            maxConnections == that.maxConnections &&
            workerThreads == that.workerThreads;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(port, backlog, maxConnections, workerThreads);
    }
    
    /**
     * ServerConfig için builder sınıfı.
     */
    public static class Builder {
        private int port = 8080;
        private int backlog = 50;
        private int maxConnections = 100;
        private int workerThreads = 10;
        
        public Builder withPort(int port) {
            this.port = port;
            return this;
        }
        
        public Builder withBacklog(int backlog) {
            this.backlog = backlog;
            return this;
        }
        
        public Builder withMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }
        
        public Builder withWorkerThreads(int workerThreads) {
            this.workerThreads = workerThreads;
            return this;
        }
        
        public ServerConfig build() {
            return new ServerConfig(this);
        }
    }
} 