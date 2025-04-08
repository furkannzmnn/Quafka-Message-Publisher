package com.quafka.monitoring;

/**
 * Bağlantı metriklerini toplayan arayüz.
 */
public interface ConnectionMetrics {
    /**
     * Bağlantı kurulduğunda metrikleri günceller.
     */
    void recordConnection();
    
    /**
     * Bağlantı kapatıldığında metrikleri günceller.
     */
    void recordDisconnection();
    
    /**
     * İşlem süresini kaydeder.
     * @param duration İşlem süresi (nanosaniye)
     */
    void recordOperation(long duration);
    
    /**
     * Hata durumunda metrikleri günceller.
     */
    void recordError();
    
    /**
     * Toplam bağlantı sayısını döndürür.
     * @return Toplam bağlantı sayısı
     */
    long getTotalConnections();
    
    /**
     * Toplam hata sayısını döndürür.
     * @return Toplam hata sayısı
     */
    long getTotalErrors();
    
    /**
     * Ortalama işlem süresini döndürür.
     * @return Ortalama işlem süresi (nanosaniye)
     */
    double getAverageOperationTime();
} 