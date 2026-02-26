package com.mcztickets.orders.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic getEventRequestTopic() {
        return TopicBuilder.name("events.get.request").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic getEventResponseTopic() {
        return TopicBuilder.name("events.get.response").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic reserveTicketsTopic() {
        return TopicBuilder.name("events.reserve.tickets").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic getOrderRequestTopic() {
        return TopicBuilder.name("orders.get.request").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic getOrderResponseTopic() {
        return TopicBuilder.name("orders.get.response").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic updateOrderStatusTopic() {
        return TopicBuilder.name("orders.update.status").partitions(1).replicas(1).build();
    }
}
