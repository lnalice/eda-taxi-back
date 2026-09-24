package net.taxiMap.taxiplatform.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Objects;

@Configuration
public class KafkaTopicConfig {
    @Value("${spring.kafka.topic.name.callResponse}")
    private String callResponse;

    @Value("${spring.kafka.topic.name.ride}")
    private String driverRide;

    @Value("${spring.kafka.topic.name.call}")
    private String call;

    @Value("${spring.kafka.topic.name.rideResponse}")
    private String rideResponse;

    @Value("${spring.kafka.topic.name.taxiLocation}")
    private String taxiLocation;

    @Value("${spring.kafka.topic.name.pickUp}")
    private String pickUp;

    @Value("${spring.kafka.topic.name.dropOff}")
    private String dropOff;

    @Bean
    public NewTopic topicCallResponse() {
        return TopicBuilder.name(Objects.requireNonNull(callResponse))
                .build();
    }

    @Bean
    public NewTopic topicRide() {
        return TopicBuilder.name(Objects.requireNonNull(driverRide))
                .build();
    }

    @Bean
    public NewTopic topicCallDlt() {
        return TopicBuilder.name(Objects.requireNonNull(call) + ".DLT")
                .build();
    }

    @Bean
    public NewTopic topicRideResponseDlt() {
        return TopicBuilder.name(Objects.requireNonNull(rideResponse) + ".DLT")
                .build();
    }

    @Bean
    public NewTopic topicTaxiLocationDlt() {
        return TopicBuilder.name(Objects.requireNonNull(taxiLocation) + ".DLT")
                .build();
    }

    @Bean
    public NewTopic topicPickUpDlt() {
        return TopicBuilder.name(Objects.requireNonNull(pickUp) + ".DLT")
                .build();
    }

    @Bean
    public NewTopic topicDropOffDlt() {
        return TopicBuilder.name(Objects.requireNonNull(dropOff) + ".DLT")
                .build();
    }
}