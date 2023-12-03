package net.taxiMap.driverservice.kafka.producer;

import net.taxiMap.basedomain.dto.event.PickUpEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class PickUpProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PickUpProducer.class);

    private final NewTopic topic;

    private final KafkaTemplate<String, PickUpEvent> pickUpTemplate;

    public PickUpProducer(@Qualifier("topicPickUp") NewTopic topic,
                                KafkaTemplate<String, PickUpEvent> pickUpTemplate) {
        this.topic = topic;
        this.pickUpTemplate = pickUpTemplate;
    }

    public void sendPickUpMessage(PickUpEvent event) {

        Message<PickUpEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();

        pickUpTemplate.send(message);

        LOGGER.info(String.format("[[ PickUp event - sent ]] %s", event));
    }
}