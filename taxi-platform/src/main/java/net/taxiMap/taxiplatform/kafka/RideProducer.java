package net.taxiMap.taxiplatform.kafka;

import net.taxiMap.basedomain.dto.event.RideEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class RideProducer {

    private final NewTopic topic;
    private final KafkaTemplate<String, RideEvent> rideTemplate;

    public RideProducer(@Qualifier("topicRide") NewTopic topic, KafkaTemplate<String, RideEvent> rideTemplate) {
        this.topic = topic;
        this.rideTemplate = rideTemplate;
    }

    public void sendRideMessage(RideEvent event) {

        Message<RideEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();

        rideTemplate.send(message);
    }
}
