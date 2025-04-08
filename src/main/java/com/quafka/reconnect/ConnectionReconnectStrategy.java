package com.quafka.reconnect;

import com.quafka.connection.Connection;
import com.quafka.exception.ConnectionException;

/**
 * Bağlantı yeniden bağlanma stratejisi arayüzü.
 */
public interface ConnectionReconnectStrategy {
    
    /**
     * Yeniden bağlanma girişiminde bulunur.
     * @param connection Yeniden bağlanılacak bağlantı
     * @return Bağlantı başarılıysa true, değilse false
     * @throws ConnectionException Yeniden bağlanma sırasında hata oluşursa
     */
    boolean attemptReconnect(Connection connection) throws ConnectionException;
    
    /**
     * Yeniden bağlanma girişimleri arasındaki bekleme süresini döndürür.
     * @param attempt Girişim sayısı
     * @return Bekleme süresi (milisaniye)
     */
    long getBackoffTime(int attempt);
    
    /**
     * Maksimum yeniden bağlanma girişimi sayısını döndürür.
     * @return Maksimum girişim sayısı
     */
    int getMaxAttempts();
} 