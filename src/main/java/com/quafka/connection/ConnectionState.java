package com.quafka.connection;

/**
 * Bağlantı durumlarını tanımlayan enum.
 */
public enum ConnectionState {
    /**
     * Bağlantı henüz başlatılmadı
     */
    INITIALIZED,
    
    /**
     * Bağlantı kuruluyor
     */
    CONNECTING,
    
    /**
     * Bağlantı kuruldu
     */
    CONNECTED,
    
    /**
     * Bağlantı kapatılıyor
     */
    DISCONNECTING,
    
    /**
     * Bağlantı kapalı
     */
    DISCONNECTED,
    
    /**
     * Bağlantı hatası
     */
    ERROR
} 