package com.quafka.loadbalancer.impl;

import com.quafka.config.ConnectionConfig;
import com.quafka.connection.Connection;
import com.quafka.connection.ConnectionFactory;
import com.quafka.connection.impl.DefaultConnectionFactory;
import com.quafka.exception.ConnectionException;
import com.quafka.loadbalancer.ConnectionLoadBalancer;
import com.quafka.loadbalancer.LoadBalancingStrategy;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sırayla bağlantı seçen yük dengeleyici uygulaması.
 */
public class RoundRobinLoadBalancer implements ConnectionLoadBalancer {
    
    private final ConnectionFactory connectionFactory;
    private final AtomicInteger currentIndex;
    
    public RoundRobinLoadBalancer() {
        this(DefaultConnectionFactory.getInstance());
    }
    
    public RoundRobinLoadBalancer(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.currentIndex = new AtomicInteger(0);
    }
    
    @Override
    public Connection getConnection(List<ConnectionConfig> configs) throws ConnectionException {
        if (configs == null || configs.isEmpty()) {
            throw new IllegalArgumentException("Bağlantı yapılandırmaları boş olamaz");
        }
        
        int index = currentIndex.getAndIncrement() % configs.size();
        ConnectionConfig config = configs.get(index);
        return connectionFactory.createConnection(config);
    }
    
    @Override
    public Connection selectConnection(List<Connection> connections) {
        if (connections == null || connections.isEmpty()) {
            throw new IllegalArgumentException("Bağlantı listesi boş olamaz");
        }
        
        int index = currentIndex.getAndIncrement() % connections.size();
        return connections.get(index);
    }
    
    @Override
    public LoadBalancingStrategy getStrategy() {
        return LoadBalancingStrategy.ROUND_ROBIN;
    }
} 