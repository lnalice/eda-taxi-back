//package net.taxiMap.taxiplatform.config;
//
//import net.taxiMap.basedomain.dto.event.RideEvent;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.ProducerFactory;
//
//@Configuration
//public class KafkaProducerConfig {
//    private final KafkaProperties kafkaProperties;
//
//    public KafkaProducerConfig(KafkaProperties kafkaProperties) {
//        this.kafkaProperties = kafkaProperties;
//    }
//
//    @Bean
//    @Qualifier("RideProducer")
//    public KafkaTemplate<String, RideEvent> rideTemplate() {
//        return new KafkaTemplate<>(rideTemplate().getProducerFactory("rideProducer"));
//    }
//
//    ProducerFactory
//}
