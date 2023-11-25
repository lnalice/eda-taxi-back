package net.taxiMap.driverservice.kafka.MessageProcessor;

import net.taxiMap.basedomain.dto.Location;
import net.taxiMap.basedomain.dto.event.CallResponseEvent;
import net.taxiMap.basedomain.dto.event.RideEvent;
import net.taxiMap.basedomain.dto.event.RideResponseEvent;
import net.taxiMap.driverservice.kafka.producer.RideResponseProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RideResponseMessager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RideResponseMessager.class);

    private final RideResponseProducer rideResponseProducer;

    public RideResponseMessager(RideResponseProducer rideResponseProducer) {
        this.rideResponseProducer = rideResponseProducer;
    }


    @KafkaListener(
            topics = "${spring.kafka.topic.name.ride}"
            , groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeRideEvent(RideEvent event) {
        LOGGER.info(String.format("[[ Ride event - received ]] %s", event.toString()));
        //[*] Produce CallResponse Event (-> user-service)

        RideResponseEvent rideResponseEvent = new RideResponseEvent(
                event.getCurrentTime(),
                event.getDriver(),
                event.getUser()
        );

        rideResponseProducer.sendRideResponseMessage(rideResponseEvent);

        LOGGER.info(String.format("[[ RideResponse Event - sent ]] %s", rideResponseEvent));
    }
}
