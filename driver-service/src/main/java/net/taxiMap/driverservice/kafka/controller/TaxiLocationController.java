package net.taxiMap.driverservice.kafka.controller;

import net.taxiMap.basedomain.dto.Driver;
import net.taxiMap.basedomain.dto.event.PickUpEvent;
import net.taxiMap.basedomain.dto.event.TaxiLocationEvent;
import net.taxiMap.driverservice.kafka.producer.PickUpProducer;
import net.taxiMap.driverservice.kafka.producer.TaxiLocationProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;


@RestController
@RequestMapping("/DRIVER")
public class TaxiLocationController {

    private final TaxiLocationProducer taxiLocationProducer;

    public TaxiLocationController(TaxiLocationProducer taxiLocationProducer) {
        this.taxiLocationProducer = taxiLocationProducer;
    }

    @PostMapping("/GPS")
    public String placeTaxiLocationController(@RequestBody Driver driver) {

        TaxiLocationEvent taxiLocationEvent = new TaxiLocationEvent();
        taxiLocationEvent.setCurrentTime(LocalTime.now());
        taxiLocationEvent.setDriver(driver);

        taxiLocationProducer.sendTaxiLocationMessage(taxiLocationEvent);

        return "Location Updated";
    }
}