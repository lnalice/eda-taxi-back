package net.taxiMap.basedomain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String userID;
    private Location currentLocation;
    private Location departure;
    private Location arrival;
}
