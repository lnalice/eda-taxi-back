package net.taxiMap.taxiplatform.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
//https://docs.spring.io/spring-boot/docs/current/reference/html/messaging.html#messaging.kafka.receiving
@Configuration
public class KafkaTopicConfig {
//    @Value("${spring.kafka.topic.name.taxiLocation}")
//    private String topicTaxiLocation;
//    @Value("${spring.kafka.topic.name.call}")
//    private String topicCall;
//    @Value("${spring.kafka.topic.name.rideResponse}")
//    private String topicRideResponse;
//    @Value("${spring.kafka.topic.name.pickUp}")
//    private String topicPickUp;
//    @Value("${spring.kafka.topic.name.dropOff}")
//    private String topicDropOff;


    // spring bean for kafka topic
    @Bean
    public NewTopic topicTaxiLocation() {
        return TopicBuilder.name("${spring.kafka.topic.name.taxiLocation}")
                .build();
    }

    @Bean
    public static NewTopic topicCall() {
        return TopicBuilder.name("${spring.kafka.topic.name.call}")
                .build();
    }

    @Bean
    public static NewTopic topicRide() {
        return TopicBuilder.name("${spring.kafka.topic.name.ride}")
                .build();
    }

    @Bean
    public static NewTopic topicRideResponse() {
        return TopicBuilder.name("${spring.kafka.topic.name.rideResponse}")
                .build();
    }

    @Bean
    public NewTopic topicPickUp() {
        return TopicBuilder.name("${spring.kafka.topic.name.pickUp}")
                .build();
    }

    @Bean
    public NewTopic topicDropOff() {
        return TopicBuilder.name("${spring.kafka.topic.name.dropOff}")
                .build();
    }
}