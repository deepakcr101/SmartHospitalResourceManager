package com.hospital.resource_manager.controller;

import com.hospital.resource_manager.domain.Doctor;
import com.hospital.resource_manager.domain.Patient;
import com.hospital.resource_manager.repository.DoctorRepository;
import com.hospital.resource_manager.repository.PatientRepository;
import com.hospital.resource_manager.repository.RoomRepository;
import com.hospital.resource_manager.service.DataUpdationService;
import com.hospital.resource_manager.service.SchedulingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/admin")
public class AdminTasksController {

    private final DataUpdationService dataUpdationService;

    public AdminTasksController(DataUpdationService dataUpdationService) {
        this.dataUpdationService = dataUpdationService;
    }

    @PostMapping("/addDoctor")
        @PreAuthorize("hasRole('ADMIN')")
        public Doctor addDoctor(@RequestBody Doctor doctor) {
            return dataUpdationService.addDoctor(doctor);
        }

    @PostMapping("/addPatient")
    public Patient addPatient(@RequestBody Patient patient) {
        dataUpdationService.addPatient(patient);
        return patient;
    }
}
