package net.taxiMap.basedomain.dto.event;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.taxiMap.basedomain.dto.Location;
import net.taxiMap.basedomain.dto.User;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallEvent {
    private LocalTime currentTime;
    private User user;
}