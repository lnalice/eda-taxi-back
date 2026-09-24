package net.taxiMap.taxiplatform.controller;

import net.taxiMap.taxiplatform.sseEmit.SseEmitters;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping(path = "/taxi")
public class RideController {

    private final SseEmitters sseEmitters;

    public RideController(SseEmitters sseEmitters) {
        this.sseEmitters = sseEmitters;
    }

    @CrossOrigin(origins = "${app.cors.allowed-origins:http://localhost:3000}")
    @GetMapping(path = "/location", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamRideEvents() {
        SseEmitter emitter = new SseEmitter(60_000L);
        sseEmitters.add(emitter);
        return emitter;
    }
}
