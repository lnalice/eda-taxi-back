package net.taxiMap.driverservice.kafka.producer;

import net.taxiMap.basedomain.dto.event.RideResponseEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class RideResponseProducer {

    private final NewTopic topic;
    private final KafkaTemplate<String, RideResponseEvent> rideResponseTemplate;

    public RideResponseProducer(@Qualifier("topicRideResponse") NewTopic topic,
                                KafkaTemplate<String, RideResponseEvent> rideResponseTemplate) {
        this.topic = topic;
        this.rideResponseTemplate = rideResponseTemplate;
    }

    public void sendRideResponseMessage(RideResponseEvent event) {

        Message<RideResponseEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();

        rideResponseTemplate.send(message);
    }
}