package com.quafka.loadbalancer.impl;

import com.quafka.config.ConnectionConfig;
import com.quafka.connection.Connection;
import com.quafka.connection.ConnectionFactory;
import com.quafka.exception.ConnectionException;
import com.quafka.loadbalancer.ConnectionLoadBalancer;
import com.quafka.loadbalancer.LoadBalancingStrategy;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * En az yüklü bağlantıyı seçen yük dengeleyici uygulaması.
 */
public class LeastLoadedLoadBalancer implements ConnectionLoadBalancer {
    
    private final ConnectionFactory connectionFactory;
    private final ConcurrentHashMap<Connection, AtomicInteger> connectionLoads;
    
    public LeastLoadedLoadBalancer(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.connectionLoads = new ConcurrentHashMap<>();
    }
    
    @Override
    public Connection getConnection(List<ConnectionConfig> configs) throws ConnectionException {
        if (configs == null || configs.isEmpty()) {
            throw new IllegalArgumentException("Bağlantı yapılandırmaları boş olamaz");
        }
        

        Connection leastLoaded = null;
        int minLoad = Integer.MAX_VALUE;
        
        for (Connection connection : connectionLoads.keySet()) {
            int load = connectionLoads.get(connection).get();
            if (load < minLoad) {
                minLoad = load;
                leastLoaded = connection;
            }
        }
        
        if (leastLoaded == null) {
            ConnectionConfig config = configs.get(0);
            leastLoaded = connectionFactory.createConnection(config);
            connectionLoads.put(leastLoaded, new AtomicInteger(0));
        }
        
        connectionLoads.get(leastLoaded).incrementAndGet();
        return leastLoaded;
    }
    
    @Override
    public Connection selectConnection(List<Connection> connections) {
        if (connections == null || connections.isEmpty()) {
            throw new IllegalArgumentException("Bağlantı listesi boş olamaz");
        }
        
        Connection leastLoaded = null;
        int minLoad = Integer.MAX_VALUE;
        
        for (Connection connection : connections) {
            AtomicInteger load = connectionLoads.get(connection);
            if (load != null && load.get() < minLoad) {
                minLoad = load.get();
                leastLoaded = connection;
            }
        }
        
        if (leastLoaded == null) {
            leastLoaded = connections.get(0);
            connectionLoads.put(leastLoaded, new AtomicInteger(0));
        }
        
        connectionLoads.get(leastLoaded).incrementAndGet();
        return leastLoaded;
    }
    
    @Override
    public LoadBalancingStrategy getStrategy() {
        return LoadBalancingStrategy.LEAST_LOADED;
    }
    
    public void decrementLoad(Connection connection) {
        AtomicInteger load = connectionLoads.get(connection);
        if (load != null) {
            load.decrementAndGet();
        }
    }
} 