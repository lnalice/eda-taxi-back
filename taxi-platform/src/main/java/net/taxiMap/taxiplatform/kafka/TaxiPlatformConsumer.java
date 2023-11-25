package net.taxiMap.taxiplatform.kafka;

import net.taxiMap.basedomain.dto.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class TaxiPlatformConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaxiPlatformConsumer.class);

    TaxiPlatformProducer taxiPlatformProducer;

    @KafkaListener(
            topics = "${spring.kafka.topic.name.taxiLocation}"
            , groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeTaxiLocationEvent(TaxiLocationEvent event) {
        LOGGER.info(String.format("TaxiLocation event received in Taxi-Platform from Driver-service => %s", event.toString()));
        //show taxiMap : Taxi real-time Location + Driver status
        //Update taxi real-time taxi information (DB)
    }


    @KafkaListener(
            topics = "${spring.kafka.topic.name.call}"
            , groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeCallEvent(CallEvent event) {
        LOGGER.info(String.format("Call event received in Taxi-Platform from User-service => %s", event.toString()));
        //[*] Produce Ride Event (-> driver-service)
//
        String nearestDriverID = "000000";

        taxiPlatformProducer.sendRideMessage(new RideEvent(
                event.getCurrentTime(),
                nearestDriverID,
                event.getUser()
        ));
        //Show TaxiMap : User call a cap now!
    }


    @KafkaListener(
            topics = "${spring.kafka.topic.name.rideResponse}"
            , groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeRideResponseEvent(RideResponseEvent event) {
        LOGGER.info(String.format("RideResponse event received in Taxi-Platform from Driver-service => %s", event.toString()));
        //[*] Produce CallResponse Event (-> user-service)

        taxiPlatformProducer.sendRideMessage(new RideEvent(
                event.getCurrentTime(),
                event.getDriver().getDriverID(),
                event.getUser()
        ));
        //Show TaxiMap : Taxi is going to the user to pick up!
        //Update Taxi Status in DB [R]
    }


    @KafkaListener(
            topics = "${spring.kafka.topic.name.pickUp}"
            , groupId = "${spring.kafka.consumer.group-id}"
    )
    public void PickUpEvent(PickUpEvent event) {
        LOGGER.info(String.format("PickUp event received in Taxi-Platform from Driver-service => %s", event.toString()));
        //Show TaxiMap : Success to pickUp & Taxi is going to destination!
        //Update Taxi Status in DB [P]
    }


    @KafkaListener(
            topics = "${spring.kafka.topic.name.dropOff}"
            , groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeDropOffEvent(DropOffEvent event) {
        LOGGER.info(String.format("DropOff event received in Taxi-Platform from Driver-service => %s", event.toString()));
        //Show TaxiMap : Taxi is arrived now!
        // [[ PAY LOGIC ]]
        //Update Taxi Status in DB [A]
    }
}
