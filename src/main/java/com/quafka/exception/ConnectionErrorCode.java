package com.quafka.exception;

/**
 * Bağlantı hata kodlarını temsil eden enum.
 */
public enum ConnectionErrorCode {
    /**
     * Bağlantı zaman aşımına uğradı
     */
    CONNECTION_TIMEOUT,
    
    /**
     * Kimlik doğrulama hatası
     */
    AUTHENTICATION_FAILED,
    
    /**
     * Yetkilendirme hatası
     */
    AUTHORIZATION_FAILED,
    
    /**
     * Ağ hatası
     */
    NETWORK_ERROR,
    
    /**
     * Sunucu hatası
     */
    SERVER_ERROR,
    
    /**
     * Protokol hatası
     */
    PROTOCOL_ERROR,
    
    /**
     * Bilinmeyen hata
     */
    UNKNOWN_ERROR,
    
    /**
     * Bağlantı havuzu dolu
     */
    POOL_FULL,
    
    /**
     * Bağlantı havuzu boş
     */
    POOL_EMPTY,
    
    /**
     * Sağlık kontrolü başarısız
     */
    HEALTH_CHECK_FAILED,
    
    /**
     * Yeniden bağlanma başarısız
     */
    RECONNECT_FAILED,
    
    /**
     * Yük dengeleme hatası
     */
    LOAD_BALANCING_ERROR
} 