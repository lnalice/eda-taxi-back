package net.taxiMap.basedomain.dto.event;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.taxiMap.basedomain.dto.Driver;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PickUpEvent {
    private LocalTime currentTime;
    private Driver driver;
}
