package com.quafka.connection;

import com.quafka.config.ConnectionConfig;
import com.quafka.exception.ConnectionException;

/**
 * Quafka bağlantı arayüzü.
 * Bu arayüz, farklı bağlantı türleri için fabrika pattern uygular.
 */
public interface ConnectionFactory {
    
    /**
     * Yeni bir bağlantı oluşturur.
     * @param config Bağlantı yapılandırması
     * @return Oluşturulan bağlantı
     * @throws ConnectionException Bağlantı oluşturulurken bir hata oluşursa
     */
    Connection createConnection(ConnectionConfig config) throws ConnectionException;
    
    /**
     * Bağlantı havuzu oluşturur.
     * @param config Bağlantı yapılandırması
     * @param poolSize Havuz boyutu
     * @return Bağlantı havuzu
     * @throws ConnectionException Havuz oluşturulurken bir hata oluşursa
     */
    ConnectionPool createConnectionPool(ConnectionConfig config, int poolSize) throws ConnectionException;
    
    /**
     * Bağlantı fabrikasını kapatır ve kaynakları serbest bırakır.
     */
    void shutdown();
} 