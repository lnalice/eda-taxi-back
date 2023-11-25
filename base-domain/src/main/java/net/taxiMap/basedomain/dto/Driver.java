package net.taxiMap.basedomain.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Driver {
    private String driverID;
    private Location currentLocation;
    private Character status;
    /*
    A: no client,
    R: accept call-reservation,
    P: success to pickup and go to dest.,
     */
}
