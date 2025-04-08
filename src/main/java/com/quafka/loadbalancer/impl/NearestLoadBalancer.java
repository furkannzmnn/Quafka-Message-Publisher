package com.quafka.loadbalancer.impl;

import com.quafka.config.ConnectionConfig;
import com.quafka.connection.Connection;
import com.quafka.connection.ConnectionFactory;
import com.quafka.exception.ConnectionException;
import com.quafka.loadbalancer.ConnectionLoadBalancer;
import com.quafka.loadbalancer.LoadBalancingStrategy;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * En yakın bağlantıyı seçen yük dengeleyici uygulaması.
 * RTT (Round Trip Time) ölçümüne göre en yakın sunucuyu seçer.
 */
public class NearestLoadBalancer implements ConnectionLoadBalancer {
    
    private final ConnectionFactory connectionFactory;
    private final ConcurrentHashMap<Connection, AtomicLong> rttMeasurements;
    private final long measurementInterval;
    
    public NearestLoadBalancer(ConnectionFactory connectionFactory) {
        this(connectionFactory, 5000); // 5 saniye
    }
    
    public NearestLoadBalancer(ConnectionFactory connectionFactory, long measurementInterval) {
        this.connectionFactory = connectionFactory;
        this.rttMeasurements = new ConcurrentHashMap<>();
        this.measurementInterval = measurementInterval;
        
        startRttMeasurementScheduler();
    }
    
    @Override
    public Connection getConnection(List<ConnectionConfig> configs) throws ConnectionException {
        if (configs == null || configs.isEmpty()) {
            throw new IllegalArgumentException("Bağlantı yapılandırmaları boş olamaz");
        }
        
        Connection nearest = null;
        long minRtt = Long.MAX_VALUE;
        
        for (Connection connection : rttMeasurements.keySet()) {
            long rtt = rttMeasurements.get(connection).get();
            if (rtt < minRtt) {
                minRtt = rtt;
                nearest = connection;
            }
        }
        
        if (nearest == null) {
            // Yeni bağlantı oluştur
            ConnectionConfig config = configs.get(0); // İlk yapılandırmayı kullan
            nearest = connectionFactory.createConnection(config);
            measureRtt(nearest);
        }
        
        return nearest;
    }
    
    @Override
    public Connection selectConnection(List<Connection> connections) {
        if (connections == null || connections.isEmpty()) {
            throw new IllegalArgumentException("Bağlantı listesi boş olamaz");
        }
        
        // En düşük RTT'ye sahip bağlantıyı bul
        Connection nearest = null;
        long minRtt = Long.MAX_VALUE;
        
        for (Connection connection : connections) {
            AtomicLong rtt = rttMeasurements.get(connection);
            if (rtt != null && rtt.get() < minRtt) {
                minRtt = rtt.get();
                nearest = connection;
            }
        }
        
        if (nearest == null) {
            nearest = connections.get(0);
            measureRtt(nearest);
        }
        
        return nearest;
    }
    
    @Override
    public LoadBalancingStrategy getStrategy() {
        return LoadBalancingStrategy.NEAREST;
    }
    
    private void startRttMeasurementScheduler() {
        Thread scheduler = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    for (Connection connection : rttMeasurements.keySet()) {
                        measureRtt(connection);
                    }
                    Thread.sleep(measurementInterval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        scheduler.setDaemon(true);
        scheduler.start();
    }
    
    private void measureRtt(Connection connection) {
        try {
            long startTime = System.nanoTime();
            
            if (connection.isConnected()) {
                long rtt = (System.nanoTime() - startTime) / 1_000_000; // milisaniyeye çevir
                rttMeasurements.computeIfAbsent(connection, k -> new AtomicLong()).set(rtt);
            }
        } catch (Exception e) {
            rttMeasurements.computeIfAbsent(connection, k -> new AtomicLong()).set(Long.MAX_VALUE);
        }
    }
} 