package net.taxiMap.userservice.kafka;

import net.taxiMap.basedomain.dto.event.CallEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class UserProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProducer.class); //use this logger to log event

    private final NewTopic topic;
    private final KafkaTemplate<String, CallEvent> callTemplate;

    public UserProducer(NewTopic topic, KafkaTemplate<String, CallEvent> callTemplate) {
        this.topic = topic;
        this.callTemplate = callTemplate;
    }

    public void sendCallMessage(CallEvent event) {
        LOGGER.info(String.format("[[ Call Event - sent ]] %s", event.toString())); //use logger to log event

        Message<CallEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();

        callTemplate.send(message);
    }
}