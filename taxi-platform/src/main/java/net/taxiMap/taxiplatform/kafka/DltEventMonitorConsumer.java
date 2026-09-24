package net.taxiMap.taxiplatform.kafka;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class DltEventMonitorConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DltEventMonitorConsumer.class);
    private static final int PAYLOAD_PREVIEW_LIMIT = 300;

    private final MeterRegistry meterRegistry;

    public DltEventMonitorConsumer(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @KafkaListener(
            topics = {
                    "${app.kafka.dlt.topic.call:Call.DLT}",
                    "${app.kafka.dlt.topic.ride:Ride.DLT}",
                    "${app.kafka.dlt.topic.rideResponse:RideResponse.DLT}",
                    "${app.kafka.dlt.topic.taxiLocation:TaxiLocation.DLT}",
                    "${app.kafka.dlt.topic.pickUp:PickUp.DLT}",
                    "${app.kafka.dlt.topic.dropOff:DropOff.DLT}",
                    "${app.kafka.dlt.topic.callResponse:CallResponse.DLT}"
            },
            groupId = "${app.kafka.dlt.consumer-group:taxi-platform-dlt-monitor}",
            containerFactory = "dltKafkaListenerContainerFactory"
    )
    public void consumeDlt(ConsumerRecord<String, byte[]> record) {
        Headers headers = record.headers();
        String sourceTopic = headerString(headers, KafkaHeaders.DLT_ORIGINAL_TOPIC, "unknown");
        String exceptionClass = headerString(headers, KafkaHeaders.DLT_EXCEPTION_FQCN, "unknown");
        String exceptionMessage = headerString(headers, KafkaHeaders.DLT_EXCEPTION_MESSAGE, "n/a");
        String consumerGroup = headerString(headers, KafkaHeaders.DLT_ORIGINAL_CONSUMER_GROUP, "unknown");
        String payloadPreview = payloadPreview(record.value());

        Counter.builder("kafka_dlt_events_total")
                .description("Total number of events moved to dead-letter topics")
                .tag("topic", record.topic())
                .tag("source_topic", sourceTopic)
                .tag("exception", exceptionClass)
                .register(meterRegistry)
                .increment();

        LOGGER.error(
                "[DLT] topic={} sourceTopic={} partition={} offset={} originalGroup={} exception={} message={} payload={}",
                record.topic(),
                sourceTopic,
                record.partition(),
                record.offset(),
                consumerGroup,
                exceptionClass,
                exceptionMessage,
                payloadPreview
        );
    }

    private String headerString(Headers headers, String key, String defaultValue) {
        if (headers == null || headers.lastHeader(key) == null || headers.lastHeader(key).value() == null) {
            return defaultValue;
        }
        return new String(headers.lastHeader(key).value(), StandardCharsets.UTF_8);
    }

    private String payloadPreview(byte[] payload) {
        if (payload == null || payload.length == 0) {
            return "<empty>";
        }
        String value = new String(payload, StandardCharsets.UTF_8);
        if (value.length() <= PAYLOAD_PREVIEW_LIMIT) {
            return value;
        }
        return value.substring(0, PAYLOAD_PREVIEW_LIMIT) + "...(truncated)";
    }
}
