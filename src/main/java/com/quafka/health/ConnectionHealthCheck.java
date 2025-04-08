package com.quafka.health;

import com.quafka.connection.Connection;

/**
 * Bağlantı sağlık kontrolü arayüzü.
 */
public interface ConnectionHealthCheck {
    
    /**
     * Bağlantının sağlıklı olup olmadığını kontrol eder.
     * @param connection Kontrol edilecek bağlantı
     * @return Bağlantı sağlıklıysa true, değilse false
     */
    boolean isHealthy(Connection connection);
    
    /**
     * Sağlık kontrolü sıklığını döndürür (milisaniye cinsinden).
     * @return Sağlık kontrolü sıklığı
     */
    long getCheckInterval();
    
    /**
     * Sağlık kontrolü zaman aşımı süresini döndürür (milisaniye cinsinden).
     * @return Zaman aşımı süresi
     */
    long getTimeout();
} 