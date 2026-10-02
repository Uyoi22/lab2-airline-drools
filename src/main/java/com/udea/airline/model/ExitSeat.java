package com.udea.airline.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kie.api.definition.type.PropertyReactive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PropertyReactive
public class ExitSeat {
    private String seatNumber;
    private boolean available;
}
