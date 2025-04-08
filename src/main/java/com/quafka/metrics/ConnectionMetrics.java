package com.quafka.metrics;

/**
 * Bağlantı metriklerini toplayan arayüz.
 */
public interface ConnectionMetrics {
    
    /**
     * Bağlantı oluşturma süresini kaydeder.
     * @param durationMs Bağlantı oluşturma süresi (milisaniye)
     */
    void recordConnectionCreation(long durationMs);
    
    /**
     * Bağlantı kapatma süresini kaydeder.
     * @param durationMs Bağlantı kapatma süresi (milisaniye)
     */
    void recordConnectionClose(long durationMs);
    
    /**
     * Bağlantı hatası sayısını artırır.
     */
    void incrementErrorCount();
    
    /**
     * Başarılı bağlantı sayısını artırır.
     */
    void incrementSuccessCount();
    
    /**
     * Bağlantı havuzu metriklerini günceller.
     * @param activeConnections Aktif bağlantı sayısı
     * @param idleConnections Boşta bekleyen bağlantı sayısı
     */
    void updatePoolMetrics(int activeConnections, int idleConnections);
} 