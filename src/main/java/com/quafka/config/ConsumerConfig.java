package com.quafka.config;

import java.util.Objects;

/**
 * Tüketici yapılandırmasını temsil eden sınıf.
 */
public class ConsumerConfig {
    private final String groupId;
    private final String topic;
    private final boolean autoCommit;
    private final long pollTimeout;
    private final long pollInterval;
    
    private ConsumerConfig(Builder builder) {
        this.groupId = builder.groupId;
        this.topic = builder.topic;
        this.autoCommit = builder.autoCommit;
        this.pollTimeout = builder.pollTimeout;
        this.pollInterval = builder.pollInterval;
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public String getTopic() {
        return topic;
    }
    
    public boolean isAutoCommit() {
        return autoCommit;
    }
    
    public long getPollTimeout() {
        return pollTimeout;
    }
    
    public long getPollInterval() {
        return pollInterval;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsumerConfig that = (ConsumerConfig) o;
        return pollTimeout == that.pollTimeout &&
            pollInterval == that.pollInterval &&
            Objects.equals(groupId, that.groupId) &&
            Objects.equals(topic, that.topic);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(groupId, topic, pollTimeout, pollInterval);
    }
    
    /**
     * ConsumerConfig için builder sınıfı.
     */
    public static class Builder {
        private String groupId;
        private String topic;
        private boolean autoCommit = true;
        private long pollTimeout = 5000;
        private long pollInterval = 1000;
        
        public Builder withGroupId(String groupId) {
            this.groupId = groupId;
            return this;
        }
        
        public Builder withTopic(String topic) {
            this.topic = topic;
            return this;
        }
        
        public Builder withAutoCommit(boolean autoCommit) {
            this.autoCommit = autoCommit;
            return this;
        }
        
        public Builder withPollTimeout(long pollTimeout) {
            this.pollTimeout = pollTimeout;
            return this;
        }
        
        public Builder withPollInterval(long pollInterval) {
            this.pollInterval = pollInterval;
            return this;
        }
        
        public ConsumerConfig build() {
            return new ConsumerConfig(this);
        }
    }
} 