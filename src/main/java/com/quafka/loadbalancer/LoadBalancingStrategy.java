package com.quafka.loadbalancer;

/**
 * Yük dengeleme stratejilerini temsil eden enum.
 */
public enum LoadBalancingStrategy {
    /**
     * Sırayla bağlantı seçer
     */
    ROUND_ROBIN,
    
    /**
     * Rastgele bağlantı seçer
     */
    RANDOM,
    
    /**
     * En az yüklü bağlantıyı seçer
     */
    LEAST_LOADED,
    
    /**
     * En yakın bağlantıyı seçer
     */
    NEAREST
} 