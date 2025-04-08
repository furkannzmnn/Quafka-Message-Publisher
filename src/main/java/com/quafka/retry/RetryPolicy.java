package com.quafka.retry;

/**
 * Yeniden deneme stratejilerini tanımlayan arayüz.
 */
public interface RetryPolicy {
    /**
     * Verilen işlemi yeniden deneme stratejisine göre çalıştırır.
     * @param operation Çalıştırılacak işlem
     * @param <T> İşlem sonucu tipi
     * @return İşlem sonucu
     * @throws Exception İşlem başarısız olduğunda
     */
    <T> T execute(ThrowingSupplier<T> operation) throws Exception;
    
    /**
     * İşlem sağlayıcı arayüzü.
     * @param <T> İşlem sonucu tipi
     */
    @FunctionalInterface
    interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
} 