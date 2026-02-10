package com.hospital.resource_manager.controller;

import com.hospital.resource_manager.domain.Doctor;
import com.hospital.resource_manager.domain.Equipment;
import com.hospital.resource_manager.domain.Patient;
import com.hospital.resource_manager.domain.Procedure;
import com.hospital.resource_manager.domain.Room;
import com.hospital.resource_manager.service.DataUpdationService;
import org.springframework.web.bind.annotation.*;
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

    @DeleteMapping("/doctor/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDoctor(@PathVariable Long id) {
        dataUpdationService.deleteDoctor(id);
    }

    @PostMapping("/addPatient")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')") // Admin and Staff can add patients
    public Patient addPatient(@RequestBody Patient patient) {
        return dataUpdationService.addPatient(patient);
    }

    @DeleteMapping("/patient/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')") // Admin and Staff can remove patients
    public void deletePatient(@PathVariable Long id) {
        dataUpdationService.deletePatient(id);
    }

    @PostMapping("/addRoom")
    @PreAuthorize("hasRole('ADMIN')")
    public Room addRoom(@RequestBody Room room) {
        return dataUpdationService.addRoom(room);
    }

    @DeleteMapping("/room/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRoom(@PathVariable Long id) {
        dataUpdationService.deleteRoom(id);
    }

    @PostMapping("/addEquipment")
    @PreAuthorize("hasRole('ADMIN')")
    public Equipment addEquipment(@RequestBody Equipment equipment) {
        return dataUpdationService.addEquipment(equipment);
    }

    @DeleteMapping("/equipment/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEquipment(@PathVariable Long id) {
        dataUpdationService.deleteEquipment(id);
    }

    @PostMapping("/addProcedure")
    @PreAuthorize("hasRole('ADMIN')")
    public Procedure addProcedure(@RequestBody Procedure procedure) {
        return dataUpdationService.addProcedure(procedure);
    }

    @DeleteMapping("/procedure/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProcedure(@PathVariable Long id) {
        dataUpdationService.deleteProcedure(id);
    }
}
