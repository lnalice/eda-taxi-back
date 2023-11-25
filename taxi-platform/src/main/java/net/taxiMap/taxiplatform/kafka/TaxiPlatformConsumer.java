package net.taxiMap.taxiplatform.kafka;

import net.taxiMap.basedomain.dto.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TaxiPlatformConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaxiPlatformConsumer.class);

//    private RideProducer rideProducer;
//
//    public TaxiPlatformConsumer(RideProducer rideProducer){
//        this.rideProducer = rideProducer;
//    }
//
//    private CallResponseProducer callResponseProducer;
//
////    public TaxiPlatformConsumer(CallResponseProducer callResponseProducer){
////        this.callResponseProducer = callResponseProducer;
////    }

    @KafkaListener(
            topics = "${spring.kafka.topic.name.taxiLocation}"
            , groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeTaxiLocationEvent(TaxiLocationEvent event) {
        LOGGER.info(String.format("[[ TaxiLocation Event - received ]] %s", event.toString()));
        //show taxiMap : Taxi real-time Location + Driver status
        //Update taxi real-time taxi information (DB)
    }


//    @KafkaListener(
//            topics = "${spring.kafka.topic.name.call}"
//            , groupId = "${spring.kafka.consumer.group-id}"
//    )
//    public void consumeCallEvent(CallEvent event) {
//        LOGGER.info(String.format("[[ Call Event - received ]] %s", event.toString()));
//        //[*] Produce Ride Event (-> driver-service)
//        String nearestDriverID = "000000";
//
//        RideEvent rideEvent = new RideEvent(
//                event.getCurrentTime(),
//                nearestDriverID,
//                event.getUser()
//        );
//
//        rideProducer.sendRideMessage(rideEvent);
//
//        LOGGER.info(String.format("[[ Ride Event - sent ]] %s", rideEvent));
//
//        //Show TaxiMap : User call a cap now!
//    }


//    @KafkaListener(
//            topics = "${spring.kafka.topic.name.rideResponse}"
//            , groupId = "${spring.kafka.consumer.group-id}"
//    )
//    public void consumeRideResponseEvent(RideResponseEvent event) {
//        LOGGER.info(String.format("[[ RideResponse event - received ]] %s", event.toString()));
//        //[*] Produce CallResponse Event (-> user-service)
//
//        CallResponseEvent callResponseEvent = new CallResponseEvent(
//                event.getCurrentTime(),
//                event.getDriver(),
//                event.getUser()
//        );
//
//        callResponseProducer.sendCallResponseMessage(callResponseEvent);
//
//        LOGGER.info(String.format("[[ CallResponse Event - sent ]] %s", callResponseEvent));
//
//        //Show TaxiMap : Taxi is going to the user to pick up!
//        //Update Taxi Status in DB [R]
//    }


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
