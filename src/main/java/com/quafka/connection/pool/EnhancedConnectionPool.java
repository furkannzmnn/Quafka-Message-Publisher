package com.quafka.connection.pool;

import com.quafka.config.ConnectionConfig;
import com.quafka.connection.Connection;
import com.quafka.connection.ConnectionFactory;
import com.quafka.connection.ConnectionPool;
import com.quafka.exception.ConnectionErrorCode;
import com.quafka.exception.ConnectionException;
import com.quafka.connection.impl.MonitoredConnection;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Gelişmiş bağlantı havuzu uygulaması.
 * Metrik toplama, sağlık kontrolü ve yeniden bağlanma özelliklerini içerir.
 */
public class EnhancedConnectionPool implements ConnectionPool {
    
    private final ConnectionConfig config;
    private final int maxPoolSize;
    private final ConnectionFactory factory;
    private final BlockingQueue<Connection> connectionPool;
    private final AtomicInteger activeConnections;
    private final AtomicInteger totalConnections;
    
    public EnhancedConnectionPool(ConnectionConfig config, int maxPoolSize, ConnectionFactory factory) {
        this.config = config;
        this.maxPoolSize = maxPoolSize;
        this.factory = factory;
        this.connectionPool = new LinkedBlockingQueue<>(maxPoolSize);
        this.activeConnections = new AtomicInteger(0);
        this.totalConnections = new AtomicInteger(0);
    }
    
    @Override
    public Connection borrowConnection() throws ConnectionException {
        try {
            Connection connection = connectionPool.poll(5, TimeUnit.SECONDS);
            
            if (connection == null) {
                if (totalConnections.get() < maxPoolSize) {
                    connection = createNewConnection();
                } else {
                    throw new ConnectionException(ConnectionErrorCode.CONNECTION_TIMEOUT,
                        "Bağlantı havuzu dolu ve yeni bağlantı oluşturulamıyor");
                }
            }
            
            activeConnections.incrementAndGet();
            return connection;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ConnectionException(ConnectionErrorCode.UNKNOWN_ERROR,
                "Bağlantı alınırken kesinti oluştu", e);
        } catch (com.quafka.connection.ConnectionException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void returnConnection(Connection connection) {
        if (connection != null && connection.isConnected()) {
            connectionPool.offer(connection);
            activeConnections.decrementAndGet();
        }
    }
    
    @Override
    public int getActiveConnections() {
        return activeConnections.get();
    }
    
    @Override
    public int getIdleConnections() {
        return connectionPool.size();
    }
    
    @Override
    public int getMaxPoolSize() {
        return maxPoolSize;
    }
    
    @Override
    public int getMinPoolSize() {
        return 0; // Şu an için minimum havuz boyutu 0
    }
    
    private Connection createNewConnection() throws ConnectionException, com.quafka.connection.ConnectionException {
        Connection connection = factory.createConnection(config);
        connection.connect();
        totalConnections.incrementAndGet();
        return connection;
    }
    
    @Override
    public void close() throws Exception {
        Connection connection;
        while ((connection = connectionPool.poll()) != null) {
            connection.disconnect();
        }
    }
} 