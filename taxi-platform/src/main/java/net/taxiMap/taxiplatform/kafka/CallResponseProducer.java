package net.taxiMap.taxiplatform.kafka;

import net.taxiMap.basedomain.dto.event.CallResponseEvent;
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
public class CallResponseProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RideProducer.class);

    private NewTopic topic;

    private KafkaTemplate<String, CallResponseEvent> callResponseTemplate;

    public CallResponseProducer(@Qualifier("topicCallResponse") NewTopic topic, KafkaTemplate<String, CallResponseEvent> callResponseTemplate) {
        this.topic = topic;
        this.callResponseTemplate = callResponseTemplate;
    }

    public void sendCallResponseMessage(CallResponseEvent event) {
        LOGGER.info(String.format("Ride event => %s", event.toString()));

        Message<CallResponseEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();

        callResponseTemplate.send(message);
    }
}
