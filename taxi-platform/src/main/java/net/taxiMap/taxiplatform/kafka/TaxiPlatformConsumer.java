package net.taxiMap.taxiplatform.kafka;

import net.taxiMap.basedomain.dto.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TaxiPlatformConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaxiPlatformConsumer.class);

    @KafkaListener(
            topics = "${spring.kafka.topic.name.taxiLocation}"
            , groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeTaxiLocationEvent(TaxiLocationEvent event) {
        LOGGER.info(String.format("[[ TaxiLocation Event - received ]] %s", event.toString()));
        //show taxiMap : Taxi real-time Location + Driver status
        //Update taxi real-time taxi information (DB)
    }


    @KafkaListener(
            topics = "${spring.kafka.topic.name.pickUp}"
            , groupId = "${spring.kafka.consumer.group-id}"
    )
    public void PickUpEvent(PickUpEvent event) {
        LOGGER.info(String.format("[[ PickUp Event - received ]] %s", event.toString()));
        //Show TaxiMap : Success to pickUp & Taxi is going to destination!
        //Update Taxi Status in DB [P]
    }


    @KafkaListener(
            topics = "${spring.kafka.topic.name.dropOff}"
            , groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeDropOffEvent(DropOffEvent event) {
        LOGGER.info(String.format("[[ DropOff Event - received ]] %s", event.toString()));
        //Show TaxiMap : Taxi is arrived now!
        // [[ PAY LOGIC ]]
        //Update Taxi Status in DB [A]
    }
}
