package com.udea.airline.controller;

import com.udea.airline.model.AirlineRequest;
import com.udea.airline.model.AirlineResponse;
import com.udea.airline.service.AirlineEvaluationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/airline")
public class AirlineController {

    private final AirlineEvaluationService service;

    public AirlineController(AirlineEvaluationService service) {
        this.service = service;
    }

    @PostMapping("/api/evaluate")
    public AirlineResponse evaluate(@RequestBody AirlineRequest request) {
        return service.evaluate(request);
    }
}
