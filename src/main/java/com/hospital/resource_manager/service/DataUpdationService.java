package com.hospital.resource_manager.service;

import com.hospital.resource_manager.domain.Patient;
import com.hospital.resource_manager.repository.DoctorRepository;
import com.hospital.resource_manager.repository.PatientRepository;
import com.hospital.resource_manager.repository.ProcedureRepository;
import com.hospital.resource_manager.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DataUpdationService {
    private static final Logger logger = LoggerFactory.getLogger(SchedulingService.class);

    private final PatientRepository patientRepo;
    private final ProcedureRepository procedureRepo;
    private final DoctorRepository doctorRepo;
    private final RoomRepository roomRepo;

    public DataUpdationService(PatientRepository patientRepo, ProcedureRepository procedureRepo, DoctorRepository doctorRepo, RoomRepository roomRepo) {
        this.patientRepo = patientRepo;
        this.procedureRepo = procedureRepo;
        this.doctorRepo = doctorRepo;
        this.roomRepo = roomRepo;
    }

    public Patient addPatient(Patient patient) {
        patientRepo.save(patient);
        return ("Patient added successfully"+ patient.getName());
    }
}
