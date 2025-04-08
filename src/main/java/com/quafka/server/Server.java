package com.quafka.server;

import com.quafka.config.ServerConfig;

/**
 * Sunucu arayüzü.
 */
public interface Server extends AutoCloseable {
    /**
     * Sunucuyu başlatır.
     * @throws Exception Başlatma sırasında hata oluşursa
     */
    void start() throws Exception;
    
    /**
     * Sunucuyu durdurur.
     * @throws Exception Durdurma sırasında hata oluşursa
     */
    void stop() throws Exception;
    
    /**
     * Sunucu yapılandırmasını döndürür.
     * @return Sunucu yapılandırması
     */
    ServerConfig getConfig();
    
    /**
     * Sunucunun çalışıp çalışmadığını döndürür.
     * @return Sunucu çalışıyorsa true
     */
    boolean isRunning();
} 