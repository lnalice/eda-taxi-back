//package net.taxiMap.driverservice.schedule;
//
//import com.google.gson.JsonParser;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//
//import net.taxiMap.basedomain.dto.Driver;
//import net.taxiMap.basedomain.dto.Location;
//import net.taxiMap.basedomain.dto.event.TaxiLocationEvent;
//import net.taxiMap.driverservice.kafka.producer.TaxiLocationProducer;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.util.FileCopyUtils;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.time.LocalTime;
//
//@Service
//public class TaxiLocationService {
//
//```
//    private final TaxiLocationProducer taxiLocationProducer;
//
//    public TaxiLocationService(TaxiLocationProducer taxiLocationProducer) {
//        this.taxiLocationProducer = taxiLocationProducer;
//    }
//
//    @Scheduled(fixedDelay = 5000)
//    private void placeTaxiLocation() throws IOException {
//
//        ClassPathResource resource = new ClassPathResource("/data/driver/driver.json");
//
//        byte[] byteData = FileCopyUtils.copyToByteArray(resource.getInputStream());
//
//        String data = new String(byteData, StandardCharsets.UTF_8);
//```
//
////        JsonParser parser = new JsonParser();
////        JsonArray driverArray = parser.parse(data).getAsJsonArray();
//
//```
//        JsonObject driverObject;
//        for (int i = 0; i < driverArray.size(); i++) {
//            driverObject = driverArray.get(i).getAsJsonObject();
//
//            logDriverInfo(driverObject);
//
//            TaxiLocationEvent taxiLocationEvent = new TaxiLocationEvent();
//
//            taxiLocationEvent.setCurrentTime(LocalTime.now());
//            taxiLocationEvent.setDriver(extractDriver(driverObject));
//
//            try{
//                taxiLocationProducer.sendTaxiLocationMessage(taxiLocationEvent);
//            } catch (NullPointerException ignored) {
//
//            }
//        }
//    }
//
//    private static Driver extractDriver(JsonObject jsonObject) {
//        Driver driver = new Driver();
//
//        driver.setDriverID(jsonObject.get("driverID").getAsString());
//
//        JsonObject locationObject = jsonObject.getAsJsonObject("currentLocation");
//        Location location = new Location();
//        location.setLatitude(locationObject.get("latitude").getAsDouble());
//        location.setLongitude(locationObject.get("longitude").getAsDouble());
//        driver.setCurrentLocation(location);
//
//        driver.setStatus(jsonObject.get("status").getAsString().charAt(0));
//
//        return driver;
//    }
//
//    private void logDriverInfo(JsonObject driverObject){
//        String driverID = driverObject.get("driverID").getAsString();
//        JsonObject currentLocation = driverObject.getAsJsonObject("currentLocation");
//        double latitude = currentLocation.get("latitude").getAsDouble();
//        double longitude = currentLocation.get("longitude").getAsDouble();
//        int pathID = driverObject.get("pathID").getAsInt();
//        String status = driverObject.get("status").getAsString();
//
//        // 로그로 출력
//        System.out.println("DriverID: " + driverID +
//                ", Latitude: " + latitude +
//                ", Longitude: " + longitude +
//                ", PathID: " + pathID +
//                ", Status: " + status);
//    }
//} 이부분 이야
//```
