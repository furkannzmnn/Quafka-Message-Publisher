package com.quafka.connection;

import com.quafka.config.ConnectionConfig;
import com.quafka.monitoring.ConnectionMetrics;
import com.quafka.monitoring.ConnectionMonitor;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Bağlantı işlemlerini tanımlayan arayüz.
 */
public interface Connection extends AutoCloseable {
    /**
     * Bağlantıyı açar.
     * @throws ConnectionException Bağlantı açılamazsa
     */
    void connect() throws ConnectionException;
    
    /**
     * Bağlantıyı kapatır.
     * @throws ConnectionException Bağlantı kapatılamazsa
     */
    void disconnect() throws ConnectionException;
    
    /**
     * Verilen işlemi senkron olarak çalıştırır.
     * @param operation Çalıştırılacak işlem
     * @param <T> İşlem sonucu tipi
     * @return İşlem sonucu
     * @throws ConnectionException İşlem başarısız olursa
     */
    <T> T execute(Supplier<T> operation) throws ConnectionException;
    
    /**
     * Verilen işlemi asenkron olarak çalıştırır.
     * @param operation Çalıştırılacak işlem
     * @param <T> İşlem sonucu tipi
     * @return İşlem sonucu için CompletableFuture
     */
    <T> CompletableFuture<T> executeAsync(Supplier<T> operation);
    
    /**
     * Mesaj gönderir ve yanıtı bekler.
     * @param message Gönderilecek mesaj
     * @return Alınan yanıt
     * @throws ConnectionException Mesaj gönderilemezse veya yanıt alınamazsa
     */
    String sendMessage(String message) throws ConnectionException;
    
    /**
     * Mesajı asenkron olarak gönderir.
     * @param message Gönderilecek mesaj
     * @return Yanıt için CompletableFuture
     */
    CompletableFuture<String> sendMessageAsync(String message);
    
    /**
     * Bağlantı durumunu döndürür.
     * @return Bağlantı durumu
     */
    ConnectionState getState();
    
    /**
     * Bağlantı yapılandırmasını döndürür.
     * @return Bağlantı yapılandırması
     */
    ConnectionConfig getConfig();
    
    /**
     * Bağlantı izleyicisini döndürür.
     * @return Bağlantı izleyicisi
     */
    ConnectionMonitor getMonitor();
    
    /**
     * Bağlantı metriklerini döndürür.
     * @return Bağlantı metrikleri
     */
    ConnectionMetrics getMetrics();
    
    /**
     * Bağlantının açık olup olmadığını döndürür.
     * @return Bağlantı açık ise true
     */
    boolean isConnected();
} 