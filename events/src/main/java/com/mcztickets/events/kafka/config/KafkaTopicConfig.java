package com.mcztickets.events.kafka.config;

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
}
