package com.quafka.retry.impl;

import com.quafka.retry.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Üstel geri çekilme (exponential backoff) stratejisi ile yeniden deneme yapan implementasyon.
 */
public class ExponentialBackoffRetryPolicy implements RetryPolicy {
    private static final Logger logger = LoggerFactory.getLogger(ExponentialBackoffRetryPolicy.class);
    
    private final int maxRetries;
    private final long initialDelay;
    private final long maxDelay;
    
    public ExponentialBackoffRetryPolicy(int maxRetries, long initialDelay, long maxDelay) {
        this.maxRetries = maxRetries;
        this.initialDelay = initialDelay;
        this.maxDelay = maxDelay;
    }
    
    @Override
    public <T> T execute(ThrowingSupplier<T> operation) throws Exception {
        int retryCount = 0;
        long delay = initialDelay;
        
        while (true) {
            try {
                return operation.get();
            } catch (Exception e) {
                if (retryCount >= maxRetries) {
                    logger.error("Maksimum yeniden deneme sayısına ulaşıldı", e);
                    throw e;
                }
                
                logger.warn("İşlem başarısız oldu, {} ms sonra yeniden denenecek (Deneme: {}/{})",
                    delay, retryCount + 1, maxRetries);
                
                Thread.sleep(delay);
                delay = Math.min(delay * 2, maxDelay);
                retryCount++;
            }
        }
    }
} 