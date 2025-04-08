package com.quafka.connection.impl;

import com.quafka.config.ConnectionConfig;
import com.quafka.connection.Connection;
import com.quafka.connection.ConnectionFactory;
import com.quafka.connection.ConnectionPool;
import com.quafka.exception.ConnectionException;
import com.quafka.connection.pool.DefaultConnectionPool;

/**
 * Quafka için varsayılan bağlantı fabrikası uygulaması.
 */
public class DefaultConnectionFactory implements ConnectionFactory {
    
    private static final DefaultConnectionFactory INSTANCE = new DefaultConnectionFactory();
    
    private DefaultConnectionFactory() {

    }
    
    public static DefaultConnectionFactory getInstance() {
        return INSTANCE;
    }
    
    @Override
    public Connection createConnection(ConnectionConfig config) throws ConnectionException {
        return new DefaultConnection(config);
    }
    
    @Override
    public ConnectionPool createConnectionPool(ConnectionConfig config, int poolSize) throws ConnectionException {
        return new DefaultConnectionPool(config, poolSize);
    }
    
    @Override
    public void shutdown() {

    }
} 