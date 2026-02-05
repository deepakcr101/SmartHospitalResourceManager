package com.hospital.resource_manager.service;

import com.hospital.resource_manager.domain.*;
import com.hospital.resource_manager.dto.ScheduleRequest;
import com.hospital.resource_manager.repository.DoctorRepository;
import com.hospital.resource_manager.repository.PatientRepository;
import com.hospital.resource_manager.repository.ProcedureRepository;
import com.hospital.resource_manager.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SchedulingService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulingService.class);

    private final PatientRepository patientRepo;
    private final ProcedureRepository procedureRepo;
    private final DoctorRepository doctorRepo;
    private final RoomRepository roomRepo;

    public SchedulingService(PatientRepository patientRepo, ProcedureRepository procedureRepo, DoctorRepository doctorRepo, RoomRepository roomRepo) {
        this.patientRepo = patientRepo;
        this.procedureRepo = procedureRepo;
        this.doctorRepo = doctorRepo;
        this.roomRepo = roomRepo;
    }

    @Transactional
    public Patient schedulePatient(ScheduleRequest request) {
        logger.debug("Attempting to schedule patient: {}", request.getPatientName());
        return schedulePatientWithFallback(request, null);
    }

    @Transactional
    public Patient schedulePatientWithFallback(ScheduleRequest request, Long excludeDoctorId) {
        logger.debug("Scheduling with fallback for patient: {}", request.getPatientName());
        
        // 1. Validate Procedure Exists
        Procedure procedure = procedureRepo.findAll().stream()
                .filter(p -> p.getName().equalsIgnoreCase(request.getProcedureName()))
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Procedure not found: {}", request.getProcedureName());
                    return new RuntimeException("Procedure not found: " + request.getProcedureName());
                });

        // 2. CONSTRAINT CHECK: Equipment Availability
        for (Equipment eq : procedure.getRequiredEquipment()) {
            if (!"AVAILABLE".equalsIgnoreCase(eq.getStatus())) {
                logger.error("Equipment {} is not available. Status: {}", eq.getName(), eq.getStatus());
                throw new RuntimeException("Scheduling Failed: Required equipment " + eq.getName() + " is " + eq.getStatus());
            }
        }

        // 3. CONSTRAINT CHECK: Find a Capable Doctor
        Doctor capableDoctor;
        boolean isSubstitute = false;
        
        if (excludeDoctorId == null) {
            capableDoctor = doctorRepo.findAvailableDoctorForProcedure(procedure.getName())
                    .orElseThrow(() -> {
                        logger.error("No available doctor found for procedure: {}", procedure.getName());
                        return new RuntimeException("Doctor not found: " + procedure.getName());
                    });
            logger.info("Found primary doctor: {}", capableDoctor.getName());
        } else {
            // Try to find substitute doctor
            capableDoctor = doctorRepo.findSubstituteDoctor(procedure.getName(), excludeDoctorId)
                    .orElseThrow(() -> {
                        logger.error("No substitute doctor found for procedure: {}", procedure.getName());
                        return new RuntimeException("No substitute doctor available for " + procedure.getName());
                    });
            isSubstitute = true;
            logger.info("Found substitute doctor: {} (primary doctor ID: {})", capableDoctor.getName(), excludeDoctorId);
        }

        // 4. CONSTRAINT CHECK: Room Availability
        Room validRoom = roomRepo.findFirstAvailableRoom(procedure.getName())
                .orElseThrow(() -> {
                    logger.error("No available room for procedure: {}", procedure.getName());
                    return new RuntimeException("Scheduling Failed: No room available for " + procedure.getName());
                });
        
        // Mark room as occupied
        validRoom.setOccupied(true);
        roomRepo.save(validRoom);
        logger.info("Room {} marked as occupied", validRoom.getName());

        // 5. EXECUTE: Create the Schedule
        Patient patient = new Patient();
        patient.setName(request.getPatientName());
        patient.setPriority(request.getPriority());

        // Link Patient -> Procedure
        patient.getScheduledProcedures().add(procedure);

        // Link Patient -> Doctor
        patient.setAssignedDoctor(capableDoctor);

        Patient savedPatient = patientRepo.save(patient);
        
        if (isSubstitute) {
            logger.warn("✅ Assigned SUBSTITUTE doctor {} to patient {}", capableDoctor.getName(), patient.getName());
        } else {
            logger.info("✅ Assigned doctor {} to patient {}", capableDoctor.getName(), patient.getName());
        }

        return savedPatient;
    }
}
