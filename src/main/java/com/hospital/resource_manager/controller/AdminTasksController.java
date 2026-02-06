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

@RestController
@RequestMapping("/api/admin")
public class AdminTasksController {

    private final DataUpdationService dataUpdationService;

    public AdminTasksController(DataUpdationService dataUpdationService) {
        this.dataUpdationService = dataUpdationService;
    }

    @PostMapping("/addDoctor")
    public Doctor addDoctor(@RequestBody Doctor doctor) {
         dataUpdationService.add
         return doctor;
    }

    @PostMapping("/addPatient")
    public Patient addPatient(@RequestBody Patient patient) {
        dataUpdationService.addPatient(patient);
        return patient;
    }
}
