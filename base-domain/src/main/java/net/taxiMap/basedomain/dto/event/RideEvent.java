package net.taxiMap.basedomain.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.taxiMap.basedomain.dto.Driver;
import net.taxiMap.basedomain.dto.User;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideEvent {
    private LocalTime currentTime;
    private String driverID;
    private User user;
}