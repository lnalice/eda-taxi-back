package net.taxiMap.driverservice.kafka.producer;

import net.taxiMap.basedomain.dto.event.DropOffEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class DropOffProducer {

    private final NewTopic topic;
    private final KafkaTemplate<String, DropOffEvent> dropOffTemplate;

    public DropOffProducer(@Qualifier("topicDropOff") NewTopic topic,
                          KafkaTemplate<String, DropOffEvent> dropOffTemplate) {
        this.topic = topic;
        this.dropOffTemplate = dropOffTemplate;
    }

    public void sendDropOffMessage(DropOffEvent event) {

        Message<DropOffEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();

        dropOffTemplate.send(message);
    }
}