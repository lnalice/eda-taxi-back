//package net.taxiMap.taxiplatform.sseEmit;
//
//import java.io.IOException;
//import lombok.extern.slf4j.Slf4j;
//import net.taxiMap.taxiplatform.kafka.TaxiPlatformConsumer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//@RestController
//@Slf4j
//@RequestMapping("/taxi")
//public class SseEmitterManager {
//
//    private final SseEmitters sseEmitters;
//
//    @Autowired
//    private TaxiPlatformConsumer taxiPlatformConsumer;
//
//
//    private String Event = null;
//
//    public String getEvent() {
//        return Event;
//    }
//
//    public void setEvent(String event) {
//        Event = event;
//    }
//
//    public SseEmitterManager(SseEmitters sseEmitters) {
//        this.sseEmitters = sseEmitters;
//    }
//
//    //    private static final Logger LOGGER = LoggerFactory.getLogger(SseEmitterManager.class);
//
//
//    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public ResponseEntity<SseEmitter> connect(String event) {
//
//        SseEmitter emitter = new SseEmitter(1000L);
//        sseEmitters.add(emitter);
//
////        LOGGER.info(String.format("[[  ]] %s", );
//
//        try {
//            while(this.getEvent() == null) {
//                emitter.send(SseEmitter.event()
//                        .name("open") //open
//                        .data("-1"));
//            }
//
//            emitter.send(SseEmitter.event()
//                    .name("taxi-event") //taxi-event
//                    .data( "event : " + this.getEvent()));
//
//            this.setEvent(null);
//
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return ResponseEntity.ok(emitter);
//    }
//}