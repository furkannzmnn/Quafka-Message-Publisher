package com.quafka.loadbalancer;

import com.quafka.connection.Connection;
import com.quafka.config.ConnectionConfig;
import com.quafka.exception.ConnectionException;

import java.util.List;

/**
 * Bağlantı yük dengeleme arayüzü.
 */
public interface ConnectionLoadBalancer {
    
    /**
     * Yeni bir bağlantı oluşturur veya mevcut bir bağlantıyı seçer.
     * @param configs Bağlantı yapılandırmaları listesi
     * @return Seçilen veya oluşturulan bağlantı
     * @throws ConnectionException Bağlantı oluşturulurken bir hata oluşursa
     */
    Connection getConnection(List<ConnectionConfig> configs) throws ConnectionException;
    
    /**
     * Bağlantı havuzundan bir bağlantı seçer.
     * @param connections Bağlantı havuzu
     * @return Seçilen bağlantı
     */
    Connection selectConnection(List<Connection> connections);
    
    /**
     * Yük dengeleme stratejisini döndürür.
     * @return Yük dengeleme stratejisi
     */
    LoadBalancingStrategy getStrategy();
} 