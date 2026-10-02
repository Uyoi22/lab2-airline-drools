package com.udea.airline.model;

import lombok.Data;
import org.kie.api.definition.type.PropertyReactive;

@Data
@PropertyReactive
public class Passenger {
    // Entrada
    private String name;
    private int age;
    private String membership = "Basic";      // Basic, Silver, Gold, Platinum
    private String seatPreference = "None";   // Any, Window, Aisle, None
    private boolean travelingWithChildren;
    private double luggageWeightKg;

    // Salida (la modifican las reglas)
    private boolean eligibleForUpgrade = true;
    private boolean upgradedToBusiness;
    private boolean priorityCheckIn;
    private double discountPercent;
    private String assignedSeat;
    private double compensation;
    private int loyaltyPoints;
    private boolean luggageAllowed = true;
    private boolean vipLoungeAccess;
}
