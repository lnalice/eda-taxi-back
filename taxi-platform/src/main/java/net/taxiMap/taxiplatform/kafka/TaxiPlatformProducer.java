package net.taxiMap.taxiplatform.kafka;

import net.taxiMap.basedomain.dto.event.*;
import net.taxiMap.taxiplatform.config.KafkaTopicConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class TaxiPlatformProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaxiPlatformProducer.class); //use this logger to log event

    private NewTopic topic;
    private KafkaTemplate<String, RideEvent> rideTemplate;
    private KafkaTemplate<String, CallResponseEvent> callResponseTemplate;

//    @Bean
//    public KafkaTemplate<String, RideEvent> rideProducer(KafkaTemplate<String, RideEvent> kafkaTemplate) {
//        this.rideTemplate = kafkaTemplate;
//        return rideTemplate;
//    }
//    @Bean
//    public KafkaTemplate<String, CallResponseEvent> ride(KafkaTemplate<String, CallResponseEvent> kafkaTemplate) {
//        this.callResponseTemplate = kafkaTemplate;
//        return callResponseTemplate;
//    }

    public void sendRideMessage(RideEvent event) {
        LOGGER.info(String.format("Ride event => %s", event.toString())); //use logger to log event

        this.topic = KafkaTopicConfig.topicRide();
        Message<RideEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();
        rideTemplate.send(message);
    }

    public void sendCallResponseMessage(CallResponseEvent event) {
        LOGGER.info(String.format("Order event => %s", event.toString())); //use logger to log event

        //create Message
        Message<CallResponseEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();
        callResponseTemplate.send(message);
    }

}
