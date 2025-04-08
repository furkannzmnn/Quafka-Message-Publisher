package com.quafka.consumer;

import com.quafka.config.ConsumerConfig;

/**
 * Mesaj tüketici arayüzü.
 */
public interface MessageConsumer extends AutoCloseable {
    /**
     * Tüketiciyi başlatır.
     * @throws Exception Başlatma sırasında hata oluşursa
     */
    void start() throws Exception;
    
    /**
     * Tüketiciyi durdurur.
     * @throws Exception Durdurma sırasında hata oluşursa
     */
    void stop() throws Exception;
    
    /**
     * Tüketici yapılandırmasını döndürür.
     * @return Tüketici yapılandırması
     */
    ConsumerConfig getConfig();
    
    /**
     * Tüketicinin çalışıp çalışmadığını döndürür.
     * @return Tüketici çalışıyorsa true
     */
    boolean isRunning();
} 