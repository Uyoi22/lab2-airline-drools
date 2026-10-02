package com.udea.airline.model;

import lombok.Data;

@Data
public class Flight {
    private String code;
    private int delayMinutes;
    private double durationHours;
}
