package net.taxiMap.basedomain.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.taxiMap.basedomain.dto.Driver;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DropOffEvent {
    private LocalTime currentTime;
    private Driver driver;
}
