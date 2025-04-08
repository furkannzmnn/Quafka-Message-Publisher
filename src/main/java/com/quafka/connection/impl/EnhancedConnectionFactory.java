package com.quafka.connection.impl;

import com.quafka.config.ConnectionConfig;
import com.quafka.connection.Connection;
import com.quafka.connection.ConnectionFactory;
import com.quafka.connection.ConnectionPool;
import com.quafka.connection.pool.EnhancedConnectionPool;
import com.quafka.exception.ConnectionErrorCode;
import com.quafka.exception.ConnectionException;
import com.quafka.health.ConnectionHealthCheck;
import com.quafka.health.impl.DefaultConnectionHealthCheck;
import com.quafka.loadbalancer.ConnectionLoadBalancer;
import com.quafka.loadbalancer.impl.RoundRobinLoadBalancer;
import com.quafka.metrics.ConnectionMetrics;
import com.quafka.metrics.impl.DefaultConnectionMetrics;
import com.quafka.reconnect.ConnectionReconnectStrategy;
import com.quafka.reconnect.impl.ExponentialBackoffReconnectStrategy;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Gelişmiş bağlantı fabrikası uygulaması.
 * Metrik toplama, sağlık kontrolü, yeniden bağlanma ve yük dengeleme özelliklerini içerir.
 */
public class EnhancedConnectionFactory implements ConnectionFactory {
    
    private final ConnectionFactory delegate;
    private final ConnectionMetrics metrics;
    private final ConnectionHealthCheck healthCheck;
    private final ConnectionReconnectStrategy reconnectStrategy;
    private final ConnectionLoadBalancer loadBalancer;
    private final ScheduledExecutorService scheduler;
    
    public EnhancedConnectionFactory() {
        this(
            DefaultConnectionFactory.getInstance(),
            new DefaultConnectionMetrics(),
            new DefaultConnectionHealthCheck(),
            new ExponentialBackoffReconnectStrategy(),
            new RoundRobinLoadBalancer()
        );
    }
    
    public EnhancedConnectionFactory(
        ConnectionFactory delegate,
        ConnectionMetrics metrics,
        ConnectionHealthCheck healthCheck,
        ConnectionReconnectStrategy reconnectStrategy,
        ConnectionLoadBalancer loadBalancer
    ) {
        this.delegate = delegate;
        this.metrics = metrics;
        this.healthCheck = healthCheck;
        this.reconnectStrategy = reconnectStrategy;
        this.loadBalancer = loadBalancer;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        
        // Sağlık kontrolü zamanlayıcısını başlat
        startHealthCheckScheduler();
    }
    
    @Override
    public Connection createConnection(ConnectionConfig config) throws ConnectionException {
        long startTime = System.currentTimeMillis();
        
        try {
            Connection connection = delegate.createConnection(config);
            connection.connect();
            
            metrics.recordConnectionCreation(System.currentTimeMillis() - startTime);
            metrics.incrementSuccessCount();
            
            return new MonitoredConnection(connection, connection.getMonitor(), null);
        } catch (Exception e) {
            metrics.incrementErrorCount();
            throw new ConnectionException(ConnectionErrorCode.UNKNOWN_ERROR,
                "Bağlantı oluşturulurken hata oluştu", e);
        }
    }
    
    @Override
    public ConnectionPool createConnectionPool(ConnectionConfig config, int poolSize) throws ConnectionException {
        return new EnhancedConnectionPool(config, poolSize, this);
    }
    
    @Override
    public void shutdown() {
        scheduler.shutdown();
        delegate.shutdown();
    }
    
    private void startHealthCheckScheduler() {
        scheduler.scheduleAtFixedRate(
            this::performHealthChecks,
            healthCheck.getCheckInterval(),
            healthCheck.getCheckInterval(),
            TimeUnit.MILLISECONDS
        );
    }
    
    private void performHealthChecks() {
        // Sağlık kontrolü mantığı burada implemente edilecek
    }
    
    public ConnectionMetrics getMetrics() {
        return metrics;
    }
    
    public ConnectionHealthCheck getHealthCheck() {
        return healthCheck;
    }
    
    public ConnectionReconnectStrategy getReconnectStrategy() {
        return reconnectStrategy;
    }
    
    public ConnectionLoadBalancer getLoadBalancer() {
        return loadBalancer;
    }
} 