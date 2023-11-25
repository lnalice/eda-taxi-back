package net.taxiMap.driverservice.kafka.producer;

import net.taxiMap.basedomain.dto.event.TaxiLocationEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class TaxiLocationProducer {

    private final NewTopic topic;
    private final KafkaTemplate<String, TaxiLocationEvent> taxiLocationEventTemplate;

    public TaxiLocationProducer(@Qualifier("topicTaxiLocation") NewTopic topic,
                                KafkaTemplate<String, TaxiLocationEvent> taxiLocationEventTemplate) {
        this.topic = topic;
        this.taxiLocationEventTemplate = taxiLocationEventTemplate;
    }

    public void sendTaxiLocationMessage(TaxiLocationEvent event) {

        Message<TaxiLocationEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();

        taxiLocationEventTemplate.send(message);
    }
}