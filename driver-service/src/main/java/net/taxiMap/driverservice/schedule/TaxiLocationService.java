package net.taxiMap.driverservice.schedule;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.annotation.PostConstruct;
import net.taxiMap.basedomain.dto.Driver;
import net.taxiMap.basedomain.dto.Location;
import net.taxiMap.basedomain.dto.event.TaxiLocationEvent;
import net.taxiMap.driverservice.kafka.producer.TaxiLocationProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;

@Service
public class TaxiLocationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaxiLocationService.class);

    private final TaxiLocationProducer taxiLocationProducer;
    private JsonArray driverArray;

    public TaxiLocationService(TaxiLocationProducer taxiLocationProducer) {
        this.taxiLocationProducer = taxiLocationProducer;
    }

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("/data/driver/driver.json");
            byte[] byteData = FileCopyUtils.copyToByteArray(resource.getInputStream());
            String data = new String(byteData, StandardCharsets.UTF_8);
            driverArray = JsonParser.parseString(data).getAsJsonArray();
            LOGGER.info("Loaded {} drivers from driver.json", driverArray.size());
        } catch (IOException e) {
            LOGGER.error("Failed to load driver.json", e);
            driverArray = new JsonArray();
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void placeTaxiLocation() {
        for (int i = 0; i < driverArray.size(); i++) {
            JsonObject driverObject = driverArray.get(i).getAsJsonObject();
            logDriverInfo(driverObject);

            TaxiLocationEvent taxiLocationEvent = new TaxiLocationEvent();
            taxiLocationEvent.setCurrentTime(LocalTime.now());
            taxiLocationEvent.setDriver(extractDriver(driverObject));

            try {
                taxiLocationProducer.sendTaxiLocationMessage(taxiLocationEvent);
            } catch (Exception e) {
                LOGGER.error("Error sending taxi location event for driver={}",
                        driverObject.get("driverID").getAsString(), e);
            }
        }
    }

    private static Driver extractDriver(JsonObject jsonObject) {
        Driver driver = new Driver();
        driver.setDriverID(jsonObject.get("driverID").getAsString());

        JsonObject locationObject = jsonObject.getAsJsonObject("currentLocation");
        Location location = new Location();
        location.setLatitude(locationObject.get("latitude").getAsDouble());
        location.setLongitude(locationObject.get("longitude").getAsDouble());
        driver.setCurrentLocation(location);

        driver.setStatus(jsonObject.get("status").getAsString().charAt(0));
        return driver;
    }

    private void logDriverInfo(JsonObject driverObject) {
        LOGGER.debug("DriverID={}, Lat={}, Lng={}, PathID={}, Status={}",
                driverObject.get("driverID").getAsString(),
                driverObject.getAsJsonObject("currentLocation").get("latitude").getAsDouble(),
                driverObject.getAsJsonObject("currentLocation").get("longitude").getAsDouble(),
                driverObject.get("pathID").getAsInt(),
                driverObject.get("status").getAsString());
    }
}
