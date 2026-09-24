package net.taxiMap.userservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Objects;

@Configuration
public class KafkaTopicConfig {
    @Value("${spring.kafka.topic.name.call}")
    private String call;

    @Value("${spring.kafka.topic.name.callResponse}")
    private String callResponse;


    @Bean
    public NewTopic topicCall() {
        return TopicBuilder.name(Objects.requireNonNull(call))
                .build();
    }

    @Bean
    public NewTopic topicCallResponseDlt() {
        return TopicBuilder.name(Objects.requireNonNull(callResponse) + ".DLT")
                .build();
    }
}