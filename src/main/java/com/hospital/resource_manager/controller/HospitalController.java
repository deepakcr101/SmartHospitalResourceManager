package com.hospital.resource_manager.controller;

import com.hospital.resource_manager.domain.Patient;
import com.hospital.resource_manager.dto.ScheduleRequest;
import com.hospital.resource_manager.dto.SchedulingResponseDto;
import com.hospital.resource_manager.service.SchedulingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hospital")
public class HospitalController {

    private static final Logger logger = LoggerFactory.getLogger(HospitalController.class);
    private final SchedulingService schedulingService;

    public HospitalController(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }

    @PostMapping("/schedule")
    public ResponseEntity<?> schedulePatient(@RequestBody ScheduleRequest request) {
        logger.debug("Received schedule request for patient: {}", request.getPatientName());
        try {
            Patient scheduledPatient = schedulingService.schedulePatient(request);
            logger.info("Patient {} scheduled successfully", request.getPatientName());
            return ResponseEntity.ok(scheduledPatient);
        } catch (RuntimeException e) {
            logger.error("Scheduling failed for patient {}: {}", request.getPatientName(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/schedule-with-fallback")
    public ResponseEntity<SchedulingResponseDto> schedulePatientWithFallback(@RequestBody ScheduleRequest request) {
        logger.debug("Received fallback schedule request for patient: {}", request.getPatientName());
        
        try {
            // Step 1: Try primary doctor
            Patient scheduledPatient = schedulingService.schedulePatient(request);
            logger.info("Patient {} scheduled with primary doctor: {}", 
                request.getPatientName(), scheduledPatient.getAssignedDoctor().getName());
            
            return ResponseEntity.ok(new SchedulingResponseDto(
                true,
                scheduledPatient,
                "Successfully scheduled with primary doctor",
                scheduledPatient.getAssignedDoctor(),
                false
            ));
        } catch (RuntimeException primaryException) {
            logger.warn("Primary doctor scheduling failed for patient {}. Attempting substitute...", 
                request.getPatientName());
            
            try {
                // Step 2: Try with substitute doctor (null means try all available)
                Patient fallbackPatient = schedulingService.schedulePatientWithFallback(request, -1L);
                logger.info("Patient {} scheduled with SUBSTITUTE doctor: {}", 
                    request.getPatientName(), fallbackPatient.getAssignedDoctor().getName());
                
                return ResponseEntity.ok(new SchedulingResponseDto(
                    true,
                    fallbackPatient,
                    "Scheduled with substitute doctor due to primary doctor unavailability",
                    fallbackPatient.getAssignedDoctor(),
                    true
                ));
            } catch (RuntimeException fallbackException) {
                logger.error("Fallback scheduling also failed for patient {}: {}", 
                    request.getPatientName(), fallbackException.getMessage());
                
                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new SchedulingResponseDto(
                        false,
                        null,
                        "Primary doctor unavailable and no substitute found. Primary error: " + 
                            primaryException.getMessage() + ". Fallback error: " + 
                            fallbackException.getMessage(),
                        null,
                        false
                    ));
            }
        }
    }
}
