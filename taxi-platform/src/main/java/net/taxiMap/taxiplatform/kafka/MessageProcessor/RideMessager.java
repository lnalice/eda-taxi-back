package net.taxiMap.taxiplatform.kafka.MessageProcessor;

import net.taxiMap.basedomain.dto.event.CallEvent;
import net.taxiMap.basedomain.dto.event.RideEvent;
import net.taxiMap.taxiplatform.kafka.RideProducer;
import org.springframework.kafka.annotation.KafkaListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RideMessager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallResponseMessager.class);
    private final RideProducer rideProducer;

    public RideMessager(RideProducer rideProducer){
        this.rideProducer = rideProducer;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.name.call}"
            , groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeCallEvent(CallEvent event) {
        LOGGER.info(String.format("[[ Call Event - received ]] %s", event.toString()));
        //[*] Produce Ride Event (-> driver-service)
        String nearestDriverID = "000000";

        RideEvent rideEvent = new RideEvent(
                event.getCurrentTime(),
                nearestDriverID,
                event.getUser()
        );

        rideProducer.sendRideMessage(rideEvent);

        LOGGER.info(String.format("[[ Ride Event - sent ]] %s", rideEvent));

        //Show TaxiMap : User call a cap now!
    }
}
