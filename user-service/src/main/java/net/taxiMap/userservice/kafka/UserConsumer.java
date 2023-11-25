package net.taxiMap.userservice.kafka;

import net.taxiMap.basedomain.dto.event.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserConsumer.class);

    @KafkaListener(
            topics = "${spring.kafka.topic.name.callResponse}"
            , groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeCallResponse(CallResponseEvent event) {
        LOGGER.info(String.format("[[ CallResponse Event - received ]] %s", event.toString()));
        //show taxiMap : User status
    }
}