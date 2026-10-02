package com.udea.airline.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AirlineResponse {
    private Passenger passenger;
    private List<String> firedRules;
}
