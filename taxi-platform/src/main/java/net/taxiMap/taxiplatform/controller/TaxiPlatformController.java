//package net.taxiMap.taxiplatform.controller;
//
//
//import net.taxiMap.basedomain.dto.User;
//import net.taxiMap.basedomain.dto.event.CallEvent;
//import net.taxiMap.taxiplatform.kafka.TaxiPlatformConsumer;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.time.LocalTime;
//import java.util.UUID;
//
////for dealing with order from client
//@RestController
//@RequestMapping("") //Request API
//
//public class TaxiPlatfromController {
//
//    private TaxiPlatformConsumer taxiPlatformConsumer;
//
////    public TaxiPlatformController(UserProducer userProducer) {
////        this.userProducer = userProducer;
////    }
//
//    @PostMapping("/CALL")
//    public String placeCall(@RequestBody User user) {
//
//        CallEvent callEvent = new CallEvent();
//        callEvent.setCurrentTime(LocalTime.now());
//        callEvent.setUser(user);
//
//        userProducer.sendCallMessage(callEvent);
//
//        return "Call Event generated";
//    }
//}
//
