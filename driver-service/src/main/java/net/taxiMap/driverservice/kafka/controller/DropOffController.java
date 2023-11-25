package net.taxiMap.driverservice.kafka.controller;

import net.taxiMap.basedomain.dto.Driver;
import net.taxiMap.basedomain.dto.event.DropOffEvent;
import net.taxiMap.driverservice.kafka.producer.DropOffProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@RestController
@RequestMapping("/DRIVER")
public class DropOffController {

    private final DropOffProducer dropOffProducer;

    public DropOffController(DropOffProducer dropOffProducer) {
        this.dropOffProducer = dropOffProducer;
    }

    @PostMapping("/DROP-OFF")
    public String placeDropOff(@RequestBody Driver driver) {

        DropOffEvent dropOffEvent = new DropOffEvent();
        dropOffEvent.setCurrentTime(LocalTime.now());
        dropOffEvent.setDriver(driver);

        dropOffProducer.sendDropOffMessage(dropOffEvent);

        return "success to drop off now";
    }
}


