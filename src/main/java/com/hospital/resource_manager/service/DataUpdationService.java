package com.hospital.resource_manager.service;

import com.hospital.resource_manager.domain.Doctor;
import com.hospital.resource_manager.domain.Equipment;
import com.hospital.resource_manager.domain.Patient;
import com.hospital.resource_manager.domain.Room;
import com.hospital.resource_manager.repository.DoctorRepository;
import com.hospital.resource_manager.repository.EquipmentRepository;
import com.hospital.resource_manager.repository.PatientRepository;
import com.hospital.resource_manager.repository.ProcedureRepository;
import com.hospital.resource_manager.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DataUpdationService {
    private static final Logger logger = LoggerFactory.getLogger(DataUpdationService.class);

    private final PatientRepository patientRepo;
    private final ProcedureRepository procedureRepo;
    private final DoctorRepository doctorRepo;
    private final RoomRepository roomRepo;
    private final EquipmentRepository equipmentRepo;

    public DataUpdationService(PatientRepository patientRepo,
                              ProcedureRepository procedureRepo,
                              DoctorRepository doctorRepo,
                              RoomRepository roomRepo,
                              EquipmentRepository equipmentRepo) {
        this.patientRepo = patientRepo;
        this.procedureRepo = procedureRepo;
        this.doctorRepo = doctorRepo;
        this.roomRepo = roomRepo;
        this.equipmentRepo = equipmentRepo;
    }

    public Patient addPatient(Patient patient) {
        return patientRepo.save(patient);
    }

    public Doctor addDoctor(Doctor doctor) {
        return doctorRepo.save(doctor);
    }

    public Optional<Doctor> updateDoctorStatus(Long doctorId, boolean active) {
        return doctorRepo.findById(doctorId).map(d -> {
            d.setActive(active);
            return doctorRepo.save(d);
        });
    }

    public Optional<Room> updateRoomOccupied(Long roomId, boolean occupied) {
        return roomRepo.findById(roomId).map(r -> {
            r.setOccupied(occupied);
            return roomRepo.save(r);
        });
    }

    public Optional<Equipment> updateEquipmentStatus(Long equipmentId, String status) {
        return equipmentRepo.findById(equipmentId).map(e -> {
            e.setStatus(status);
            return equipmentRepo.save(e);
        });
    }
}
