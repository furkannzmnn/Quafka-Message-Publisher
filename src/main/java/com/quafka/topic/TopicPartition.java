package com.quafka.topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Topic partition'ını temsil eden sınıf.
 */
public class TopicPartition {
    private static final Logger logger = LoggerFactory.getLogger(TopicPartition.class);
    
    private final String topic;
    private final int partitionId;
    private final BlockingQueue<String> messages;
    private final AtomicLong offset;
    
    public TopicPartition(String topic, int partitionId) {
        this.topic = topic;
        this.partitionId = partitionId;
        this.messages = new LinkedBlockingQueue<>();
        this.offset = new AtomicLong(0);
    }
    
    /**
     * Partition'a mesaj ekler.
     * @param message Mesaj içeriği
     * @return Mesajın offset'i
     */
    public long publish(String message) {
        messages.offer(message);
        long currentOffset = offset.getAndIncrement();
        logger.debug("Topic '{}' partition {} için yeni mesaj eklendi (offset: {}): {}", 
            topic, partitionId, currentOffset, message);
        return currentOffset;
    }
    
    /**
     * Partition'dan mesaj alır.
     * @return Alınan mesaj ve offset'i, mesaj yoksa null
     */
    public MessageWithOffset poll() {
        String message = messages.poll();
        if (message != null) {
            logger.debug("Topic '{}' partition {} için mesaj alındı: {}", 
                topic, partitionId, message);
            return new MessageWithOffset(message, offset.get() - 1);
        }
        return null;
    }
    
    /**
     * Belirli bir offset'ten sonraki mesajı alır.
     * @param fromOffset Başlangıç offset'i
     * @return Alınan mesaj ve offset'i, mesaj yoksa null
     */
    public MessageWithOffset poll(long fromOffset) {
        // Basit implementasyon: Sadece son mesajı döndür
        if (fromOffset < offset.get() - 1) {
            String message = messages.peek();
            return new MessageWithOffset(message, offset.get() - 1);
        }
        return null;
    }
    
    /**
     * Partition'daki mesaj sayısını döndürür.
     * @return Mesaj sayısı
     */
    public int getMessageCount() {
        return messages.size();
    }
    
    /**
     * Son offset'i döndürür.
     * @return Son offset
     */
    public long getLastOffset() {
        return offset.get() - 1;
    }
    
    public String getTopic() {
        return topic;
    }
    
    public int getPartitionId() {
        return partitionId;
    }
    
    /**
     * Mesaj ve offset bilgisini tutan iç sınıf.
     */
    public static class MessageWithOffset {
        private final String message;
        private final long offset;
        
        public MessageWithOffset(String message, long offset) {
            this.message = message;
            this.offset = offset;
        }
        
        public String getMessage() {
            return message;
        }
        
        public long getOffset() {
            return offset;
        }
    }
} 