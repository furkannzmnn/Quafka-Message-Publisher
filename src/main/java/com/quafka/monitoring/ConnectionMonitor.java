package com.quafka.monitoring;

import com.quafka.connection.ConnectionState;

/**
 * Bağlantı izleme işlemlerini tanımlayan arayüz.
 */
public interface ConnectionMonitor {
    /**
     * Bağlantı durumunu izler.
     * @param state Yeni bağlantı durumu
     */
    void monitorState(ConnectionState state);
    
    /**
     * Bağlantı hatasını izler.
     * @param error Hata mesajı
     */
    void monitorError(String error);
    
    /**
     * Bağlantı performansını izler.
     * @param operationTime İşlem süresi (nanosaniye)
     */
    void monitorPerformance(long operationTime);
} 