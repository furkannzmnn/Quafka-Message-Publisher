package com.quafka.connection;

import com.quafka.exception.ConnectionException;

/**
 * ConnectionPool arayüzü.
 * Bu arayüz, bağlantı havuzu yönetimi için gerekli işlemleri tanımlar.
 */
public interface ConnectionPool extends AutoCloseable {
    
    /**
     * Havuzdan bir bağlantı alır.
     * @return Havuzdan alınan bağlantı
     * @throws ConnectionException Bağlantı alınırken bir hata oluşursa
     */
    Connection borrowConnection() throws ConnectionException, com.quafka.connection.ConnectionException;
    
    /**
     * Bağlantıyı havuza geri verir.
     * @param connection Havuza geri verilecek bağlantı
     */
    void returnConnection(Connection connection);
    
    /**
     * Havuzdaki aktif bağlantı sayısını döndürür.
     * @return Aktif bağlantı sayısı
     */
    int getActiveConnections();
    
    /**
     * Havuzdaki boşta bekleyen bağlantı sayısını döndürür.
     * @return Boşta bekleyen bağlantı sayısı
     */
    int getIdleConnections();
    
    /**
     * Havuzun maksimum boyutunu döndürür.
     * @return Maksimum havuz boyutu
     */
    int getMaxPoolSize();
    
    /**
     * Havuzun minimum boyutunu döndürür.
     * @return Minimum havuz boyutu
     */
    int getMinPoolSize();
} 