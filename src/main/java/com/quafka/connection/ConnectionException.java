package com.quafka.connection;

/**
 * Bağlantı işlemleri sırasında oluşabilecek hataları temsil eden istisna sınıfı.
 */
public class ConnectionException extends Exception {
    public ConnectionException(String message) {
        super(message);
    }
    
    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
} 