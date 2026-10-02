package com.udea.airline;

import static org.junit.jupiter.api.Assertions.*;

import com.udea.airline.model.*;
import com.udea.airline.service.AirlineEvaluationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AirlineRulesTest {

    @Autowired AirlineEvaluationService service;

    private AirlineRequest req(String membership, int age, double kg, String seat, boolean kids,
                               int delay, double hours, boolean exitSeat) {
        Passenger p = new Passenger();
        p.setName("Test"); p.setMembership(membership); p.setAge(age);
        p.setLuggageWeightKg(kg); p.setSeatPreference(seat); p.setTravelingWithChildren(kids);
        Flight f = new Flight(); f.setCode("AV100"); f.setDelayMinutes(delay); f.setDurationHours(hours);
        AirlineRequest r = new AirlineRequest();
        r.setPassenger(p); r.setFlight(f); r.setEmergencyExitSeatAvailable(exitSeat);
        return r;
    }

    @Test void upgradeGoldWithDelay() {
        Passenger p = service.evaluate(req("Gold", 30, 12, "None", false, 90, 3, false)).getPassenger();
        assertTrue(p.isUpgradedToBusiness());
    }

    @Test void overweightBlocksUpgrade() {
        Passenger p = service.evaluate(req("Platinum", 30, 25, "None", false, 90, 3, false)).getPassenger();
        assertFalse(p.isEligibleForUpgrade());
        assertFalse(p.isUpgradedToBusiness());
        assertTrue(p.isVipLoungeAccess());
    }

    @Test void seniorLightLuggage() {
        Passenger p = service.evaluate(req("Basic", 70, 5, "None", false, 0, 3, false)).getPassenger();
        assertTrue(p.isPriorityCheckIn());
        assertEquals(10.0, p.getDiscountPercent());
    }

    @Test void youngAdultGetsExitSeat() {
        Passenger p = service.evaluate(req("Basic", 25, 12, "Any", false, 0, 3, true)).getPassenger();
        assertEquals("14A", p.getAssignedSeat());
    }

    @Test void extremeDelayAndLongFlight() {
        Passenger p = service.evaluate(req("Silver", 30, 12, "None", false, 200, 6, false)).getPassenger();
        assertEquals(200.0, p.getCompensation());
        assertEquals(500, p.getLoyaltyPoints());
    }

    @Test void shortFlightHeavyLuggage() {
        Passenger p = service.evaluate(req("Basic", 30, 18, "None", false, 0, 1.5, false)).getPassenger();
        assertFalse(p.isLuggageAllowed());
    }

    @Test void familySeat() {
        Passenger p = service.evaluate(req("Basic", 35, 12, "None", true, 0, 3, false)).getPassenger();
        assertEquals("Asiento preferencial familiar", p.getAssignedSeat());
    }
}
