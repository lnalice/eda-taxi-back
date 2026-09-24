package net.taxiMap.taxiplatform.controller;

import net.taxiMap.basedomain.dto.event.RideEvent;
import net.taxiMap.taxiplatform.kafka.MessageProcessor.RideMessager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Controller
@RequestMapping(path = "/taxi")
public class RideController {

    private RideMessager rideMessager;


    @CrossOrigin(origins = "http://localhost:3000/")
    @GetMapping(path = "/location", produces = MediaType.APPLICATION_JSON_VALUE,
            headers = HttpHeaders.CONNECTION+"="+"keep-alive")
    public void showRideEvent(RideEvent rideEvent) throws IOException {

        //Show TaxiMap : User call a cap now!
        SseEmitter emitter = new SseEmitter();
        emitter.send(SseEmitter.event()
                .id(rideEvent.getDriver().getDriverID())
                .data(String.valueOf(rideEvent.toString())), MediaType.TEXT_EVENT_STREAM);
    }
}
