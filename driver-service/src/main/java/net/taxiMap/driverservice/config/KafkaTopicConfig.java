package net.taxiMap.driverservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Objects;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topic.name.taxiLocation}")
    private String taxiLocation;

    @Value("${spring.kafka.topic.name.rideResponse}")
    private String rideResponse;

    @Value("${spring.kafka.topic.name.pickUp}")
    private String pickUp;

    @Value("${spring.kafka.topic.name.dropOff}")
    private String dropOff;

    @Value("${spring.kafka.topic.name.ride}")
    private String ride;


    @Bean
    public NewTopic topicTaxiLocation() {
        return TopicBuilder.name(Objects.requireNonNull(taxiLocation))
                .build();
    }

    @Bean
    public NewTopic topicRideResponse() {
        return TopicBuilder.name(Objects.requireNonNull(rideResponse))
                .build();
    }

    @Bean
    public NewTopic topicPickUp() {
        return TopicBuilder.name(Objects.requireNonNull(pickUp))
                .build();
    }

    @Bean
    public NewTopic topicDropOff() {
        return TopicBuilder.name(Objects.requireNonNull(dropOff))
                .build();
    }

    @Bean
    public NewTopic topicRideDlt() {
        return TopicBuilder.name(Objects.requireNonNull(ride) + ".DLT")
                .build();
    }
}