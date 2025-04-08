package com.quafka.exception;

/**
 * Quafka bağlantı işlemleri sırasında oluşabilecek istisnaları temsil eden sınıf.
 */
public class ConnectionException extends Exception {
    
    private final ConnectionErrorCode errorCode;
    
    public ConnectionException(ConnectionErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ConnectionException(ConnectionErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public ConnectionErrorCode getErrorCode() {
        return errorCode;
    }
} 