package com.hospital.resource_manager.controller;

import com.hospital.resource_manager.domain.*;
import com.hospital.resource_manager.repository.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/verify")
public class VerificationController {

    private final DoctorRepository doctorRepo;
    private final ProcedureRepository procedureRepo;
    private final EquipmentRepository equipmentRepo;
    private final RoomRepository roomRepo;

    public VerificationController(DoctorRepository doctorRepo,
                                  ProcedureRepository procedureRepo,
                                  EquipmentRepository equipmentRepo,
                                  RoomRepository roomRepo) {
        this.doctorRepo = doctorRepo;
        this.procedureRepo = procedureRepo;
        this.equipmentRepo = equipmentRepo;
        this.roomRepo = roomRepo;
    }

    @GetMapping("/doctors")
    public List<Doctor> getAllDoctors() {
        // This will show us if the 'knownProcedures' relationship is actually loaded
        return doctorRepo.findAll();
    }

    @GetMapping("/procedures")
    public List<Procedure> getAllProcedures() {
        return procedureRepo.findAll();
    }

    @GetMapping("/equipment")
    public List<Equipment> getAllEquipment() {
        return equipmentRepo.findAll();
    }

    @GetMapping("/rooms")
    public List<Room> getAllRooms() {
        return roomRepo.findAll();
    }
}