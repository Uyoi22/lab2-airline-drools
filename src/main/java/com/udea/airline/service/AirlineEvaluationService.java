package com.udea.airline.service;

import com.udea.airline.model.*;
import java.util.ArrayList;
import java.util.List;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

@Service
public class AirlineEvaluationService {

    private final KieContainer kieContainer;

    public AirlineEvaluationService(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    public AirlineResponse evaluate(AirlineRequest request) {
        List<String> fired = new ArrayList<>();
        KieSession session = kieContainer.newKieSession();
        try {
            session.addEventListener(new DefaultAgendaEventListener() {
                @Override
                public void afterMatchFired(AfterMatchFiredEvent e) {
                    fired.add(e.getMatch().getRule().getName());
                }
            });
            session.insert(request.getPassenger());
            session.insert(request.getFlight());
            session.insert(new ExitSeat("14A", request.isEmergencyExitSeatAvailable()));
            session.fireAllRules();
        } finally {
            session.dispose();
        }
        return new AirlineResponse(request.getPassenger(), fired);
    }
}
