package com.udea.airline.model;

import lombok.Data;

@Data
public class AirlineRequest {
    private Passenger passenger;
    private Flight flight;
    private boolean emergencyExitSeatAvailable;
}
