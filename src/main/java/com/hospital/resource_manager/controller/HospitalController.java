package com.hospital.resource_manager.controller;

import com.hospital.resource_manager.domain.Patient;
import com.hospital.resource_manager.dto.ScheduleRequest;
import com.hospital.resource_manager.service.SchedulingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hospital")
public class HospitalController {

    private final SchedulingService schedulingService;

    public HospitalController(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }

    @PostMapping("/schedule")
    public ResponseEntity<?> schedulePatient(@RequestBody ScheduleRequest request) {
        try {
            Patient scheduledPatient = schedulingService.schedulePatient(request);
            return ResponseEntity.ok(scheduledPatient);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
