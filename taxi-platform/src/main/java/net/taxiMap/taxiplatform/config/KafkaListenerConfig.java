package net.taxiMap.taxiplatform.config;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
public class KafkaListenerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaListenerConfig.class);

    @Value("${app.kafka.retry.max-attempts:3}")
    private int maxAttempts;

    @Value("${app.kafka.retry.initial-interval-ms:1000}")
    private long initialIntervalMs;

    @Value("${app.kafka.retry.multiplier:2.0}")
    private double multiplier;

    @Value("${app.kafka.retry.max-interval-ms:10000}")
    private long maxIntervalMs;

    @Bean
    @SuppressWarnings("null")
    public DefaultErrorHandler kafkaErrorHandler(
            KafkaOperations<? extends Object, ? extends Object> kafkaOperations
    ) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaOperations,
                (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition())
        );

        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(maxAttempts);
        backOff.setInitialInterval(initialIntervalMs);
        backOff.setMultiplier(multiplier);
        backOff.setMaxInterval(maxIntervalMs);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
        errorHandler.setRetryListeners((ConsumerRecord<?, ?> record, Exception ex, int deliveryAttempt) ->
                LOGGER.warn(
                        "Kafka retry attempt={} topic={} partition={} offset={} reason={}",
                        deliveryAttempt, record.topic(), record.partition(), record.offset(), ex.getMessage()
                )
        );
        return errorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> consumerFactory,
            DefaultErrorHandler kafkaErrorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, consumerFactory);
        factory.setCommonErrorHandler(Objects.requireNonNull(kafkaErrorHandler));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, byte[]> dltConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> properties = new HashMap<>(kafkaProperties.buildConsumerProperties());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    public CommonErrorHandler dltCommonErrorHandler() {
        ConsumerRecordRecoverer recoverer = (record, ex) ->
                LOGGER.error(
                        "DLT monitor failed and skipped topic={} partition={} offset={} reason={}",
                        record.topic(), record.partition(), record.offset(), ex.getMessage(), ex
                );
        return new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 0L));
    }

    @Bean
    @SuppressWarnings("null")
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> dltKafkaListenerContainerFactory(
            ConsumerFactory<String, byte[]> dltConsumerFactory,
            CommonErrorHandler dltCommonErrorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(Objects.requireNonNull(dltConsumerFactory));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        factory.setCommonErrorHandler(Objects.requireNonNull(dltCommonErrorHandler));
        return factory;
    }
}
