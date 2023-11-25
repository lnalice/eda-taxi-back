package net.taxiMap.taxiplatform.kafka.MessageProcessor;

import net.taxiMap.basedomain.dto.event.CallResponseEvent;
import net.taxiMap.basedomain.dto.event.RideResponseEvent;
import net.taxiMap.taxiplatform.kafka.CallResponseProducer;
import org.springframework.kafka.annotation.KafkaListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CallResponseMessager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallResponseMessager.class);

    private final CallResponseProducer callResponseProducer;

    public CallResponseMessager(CallResponseProducer callResponseProducer) {
        this.callResponseProducer = callResponseProducer;
    }


    @KafkaListener(
            topics = "${spring.kafka.topic.name.rideResponse}"
            , groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeRideResponseEvent(RideResponseEvent event) {
        LOGGER.info(String.format("[[ RideResponse event - received ]] %s", event.toString()));
        //[*] Produce CallResponse Event (-> user-service)

        CallResponseEvent callResponseEvent = new CallResponseEvent(
                event.getCurrentTime(),
                event.getDriver(),
                event.getUser()
        );

        callResponseProducer.sendCallResponseMessage(callResponseEvent);

        LOGGER.info(String.format("[[ CallResponse Event - sent ]] %s", callResponseEvent));

        //Show TaxiMap : Taxi is going to the user to pick up!
        //Update Taxi Status in DB [R]
    }
}
