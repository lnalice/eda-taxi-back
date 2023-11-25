package net.taxiMap.taxiplatform.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Value("${spring.kafka.topic.name.callResponse}")
    private String callResponse;
    @Value("${spring.kafka.topic.name.ride}")
    private String driverRide;

    @Bean
    public NewTopic topicCallResponse() {
        return TopicBuilder.name(callResponse)
                .build();
    }

    @Bean
    public NewTopic topicRide() {
        return TopicBuilder.name(driverRide)
                .build();
    }
}