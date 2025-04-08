package com.quafka.topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Topic yönetimi için merkezi sınıf.
 */
public class TopicManager {
    private static final Logger logger = LoggerFactory.getLogger(TopicManager.class);
    private static final TopicManager INSTANCE = new TopicManager();
    
    private final Map<String, List<TopicPartition>> topics;
    private final int defaultPartitionCount;
    
    private TopicManager() {
        this.topics = new ConcurrentHashMap<>();
        this.defaultPartitionCount = 3; // Varsayılan partition sayısı
    }
    
    public static TopicManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Topic'e mesaj ekler.
     * @param topic Topic adı
     * @param message Mesaj içeriği
     * @return Mesajın eklendiği partition ve offset bilgisi
     */
    public PartitionOffset publish(String topic, String message) {
        List<TopicPartition> partitions = getOrCreatePartitions(topic);
        
        int partitionId = ThreadLocalRandom.current().nextInt(partitions.size());
        TopicPartition partition = partitions.get(partitionId);
        
        long offset = partition.publish(message);
        return new PartitionOffset(partitionId, offset);
    }
    
    /**
     * Topic'ten mesaj alır.
     * @param topic Topic adı
     * @param partitionId Partition ID
     * @param offset Başlangıç offset'i (-1 ise son mesaj)
     * @return Alınan mesaj ve offset bilgisi
     */
    public TopicPartition.MessageWithOffset poll(String topic, int partitionId, long offset) {
        List<TopicPartition> partitions = topics.get(topic);
        if (partitions == null || partitionId >= partitions.size()) {
            logger.debug("Topic '{}' partition {} bulunamadı", topic, partitionId);
            return null;
        }
        
        TopicPartition partition = partitions.get(partitionId);
        return offset == -1 ? 
            partition.poll() : 
            partition.poll(offset);
    }
    
    /**
     * Topic'in partition sayısını döndürür.
     * @param topic Topic adı
     * @return Partition sayısı
     */
    public int getPartitionCount(String topic) {
        List<TopicPartition> partitions = topics.get(topic);
        return partitions != null ? partitions.size() : 0;
    }
    
    /**
     * Topic'in mesaj sayısını döndürür.
     * @param topic Topic adı
     * @return Mesaj sayısı
     */
    public int getMessageCount(String topic) {
        List<TopicPartition> partitions = topics.get(topic);
        if (partitions == null) {
            return 0;
        }
        
        return partitions.stream()
            .mapToInt(TopicPartition::getMessageCount)
            .sum();
    }
    
    /**
     * Topic'i siler.
     * @param topic Topic adı
     */
    public void deleteTopic(String topic) {
        topics.remove(topic);
        logger.info("Topic '{}' silindi", topic);
    }
    
    private List<TopicPartition> getOrCreatePartitions(String topic) {
        return topics.computeIfAbsent(topic, k -> {
            List<TopicPartition> partitions = new ArrayList<>(defaultPartitionCount);
            for (int i = 0; i < defaultPartitionCount; i++) {
                partitions.add(new TopicPartition(topic, i));
            }
            logger.info("Topic '{}' için {} partition oluşturuldu", topic, defaultPartitionCount);
            return partitions;
        });
    }
    
    /**
     * Partition ve offset bilgisini tutan sınıf.
     */
    public static class PartitionOffset {
        private final int partitionId;
        private final long offset;
        
        public PartitionOffset(int partitionId, long offset) {
            this.partitionId = partitionId;
            this.offset = offset;
        }
        
        public int getPartitionId() {
            return partitionId;
        }
        
        public long getOffset() {
            return offset;
        }
    }
} 