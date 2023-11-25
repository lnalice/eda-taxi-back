package net.taxiMap.driverservice.kafka.controller;

import net.taxiMap.basedomain.dto.Driver;
import net.taxiMap.basedomain.dto.event.PickUpEvent;
import net.taxiMap.driverservice.kafka.producer.PickUpProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@RestController
@RequestMapping("/DRIVER")
public class PickUpController {

    private final PickUpProducer pickUpProducer;

    public PickUpController(PickUpProducer pickUpProducer) {
        this.pickUpProducer = pickUpProducer;
    }

    @PostMapping("/PICK-UP")
    public String placePickUp(@RequestBody Driver driver) {

        PickUpEvent pickUpEvent = new PickUpEvent();
        pickUpEvent.setCurrentTime(LocalTime.now());
        pickUpEvent.setDriver(driver);

        pickUpProducer.sendPickUpMessage(pickUpEvent);

        return "success to pick up now";
    }
}


