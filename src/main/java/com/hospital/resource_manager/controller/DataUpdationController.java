package com.hospital.resource_manager.controller;

import com.hospital.resource_manager.domain.Doctor;
import com.hospital.resource_manager.domain.Equipment;
import com.hospital.resource_manager.domain.Patient;
import com.hospital.resource_manager.domain.Room;
import com.hospital.resource_manager.service.DataUpdationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/data")
public class DataUpdationController {

    private final DataUpdationService dataUpdationService;

    public DataUpdationController(DataUpdationService dataUpdationService) {
        this.dataUpdationService = dataUpdationService;
    }

    @PostMapping("/doctor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Doctor> addDoctor(@RequestBody Doctor doctor){
        return ResponseEntity.ok(dataUpdationService.addDoctor(doctor));
    }

    @PostMapping("/patient")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<Patient> addPatient(@RequestBody Patient patient){
        return ResponseEntity.ok(dataUpdationService.addPatient(patient));
    }

    @PutMapping("/doctor/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<?> updateDoctorStatus(@PathVariable Long id, @RequestBody Map<String, Object> body){
        boolean active = Boolean.parseBoolean(String.valueOf(body.getOrDefault("active", "true")));
        return dataUpdationService.updateDoctorStatus(id, active)
                .map(d -> ResponseEntity.ok(d))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/room/{id}/occupy")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<?> updateRoomOccupied(@PathVariable Long id, @RequestBody Map<String, Object> body){
        boolean occupied = Boolean.parseBoolean(String.valueOf(body.getOrDefault("occupied", "true")));
        return dataUpdationService.updateRoomOccupied(id, occupied)
                .map(r -> ResponseEntity.ok(r))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/equipment/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<?> updateEquipmentStatus(@PathVariable Long id, @RequestBody Map<String, Object> body){
        String status = String.valueOf(body.getOrDefault("status", "AVAILABLE"));
        return dataUpdationService.updateEquipmentStatus(id, status)
                .map(e -> ResponseEntity.ok(e))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
