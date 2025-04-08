package com.modulith;

import com.quafka.config.ConnectionConfig;
import com.quafka.config.ConsumerConfig;
import com.quafka.config.ServerConfig;
import com.quafka.connection.impl.DefaultConnection;
import com.quafka.server.Server;
import com.quafka.server.impl.DefaultServer;
import com.quafka.consumer.MessageConsumer;
import com.quafka.consumer.impl.DefaultMessageConsumer;

public class Main {
    public static void main(String[] args) throws Exception {
        ServerConfig serverConfig = new ServerConfig.Builder()
                .withPort(8080)
                .build();

        Server server = new DefaultServer(serverConfig);
        server.start();

        ConsumerConfig consumerConfig = new ConsumerConfig.Builder()
                .withGroupId("test-group")
                .withTopic("test-topic")
                .withAutoCommit(true)
                .withPollTimeout(5000)
                .withPollInterval(1000)
                .build();

        // Tüketiciyi başlat
        MessageConsumer consumer = new DefaultMessageConsumer(consumerConfig);
        consumer.start();

        // İstemci bağlantısı
        ConnectionConfig clientConfig = new ConnectionConfig.Builder()
                .withHost("localhost")
                .withPort(8080)
                .withTimeout(5000)
                .build();

        try (DefaultConnection connection = new DefaultConnection(clientConfig)) {
            connection.connect();

            // Mesaj gönder
            String response = connection.sendMessageAsync("TEXT:Merhaba!").get();
            System.out.println("Yanıt: " + response);

            // Tüketicinin mesajları işlemesi için biraz bekle
            Thread.sleep(5000);
        }


    }
}