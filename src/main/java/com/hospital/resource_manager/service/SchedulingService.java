package com.hospital.resource_manager.service;

import com.hospital.resource_manager.domain.*;
import com.hospital.resource_manager.dto.ScheduleRequest;
import com.hospital.resource_manager.repository.DoctorRepository;
import com.hospital.resource_manager.repository.PatientRepository;
import com.hospital.resource_manager.repository.ProcedureRepository;
import com.hospital.resource_manager.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class SchedulingService {

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
        // 1. Validate Procedure Exists
        Procedure procedure = procedureRepo.findAll().stream()
                .filter(p -> p.getName().equalsIgnoreCase(request.getProcedureName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Procedure not found: " + request.getProcedureName()));

        // 2. CONSTRAINT CHECK: Equipment Availability
        // We look at what the procedure REQUIRES and check status
        for (Equipment eq : procedure.getRequiredEquipment()) {
            if (!"AVAILABLE".equalsIgnoreCase(eq.getStatus())) {
                throw new RuntimeException("Scheduling Failed: Required equipment " + eq.getName() + " is " + eq.getStatus());
            }
        }

        // 3. CONSTRAINT CHECK: Find a Capable Doctor
        // This is a naive filter. Later we will use a custom Cypher query for speed.
        /*Doctor capableDoctor = doctorRepo.findAll().stream()
                .filter(Doctor::isActive)
                .filter(doc -> doc.getKnownProcedures().contains(procedure))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Scheduling Failed: No active doctor found for " + procedure.getName()));*/

        Doctor capableDoctor= doctorRepo.findAvailableDoctorForProcedure(procedure.getName()).orElseThrow(() -> new RuntimeException("Doctor not found: " + procedure.getName()));

        // 4. CONSTRAINT CHECK: Room Availability
// We need a room that is:
// a) SUITABLE for the procedure
// b) NOT Occupied
// Note: In a real app, we would query the repo directly.
// For now, we filter in Java to keep logic visible.

//        Room validRoom = roomRepo.findAll().stream()
//                .filter(r -> !r.isOccupied()) // Must be empty
//                .filter(r -> r.getSuitableProcedures().contains(procedure)) // Must handle this surgery
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("Scheduling Failed: No suitable room available for " + procedure.getName()));

        Room validRoom= roomRepo.findFirstAvailableRoom(procedure.getName()).orElseThrow(() ->
                new RuntimeException("Scheduling Failed: No room available for " + procedure.getName()));
// Mark room as occupied (Atomic transaction handles this!)
        validRoom.setOccupied(true);
        roomRepo.save(validRoom);


        // 5. EXECUTE: Create the Schedule
        Patient patient = new Patient();
        patient.setName(request.getPatientName());
        patient.setPriority(request.getPriority());

        // Link Patient -> Procedure
        patient.getScheduledProcedures().add(procedure);

        // Link Patient -> Doctor
        patient.setAssignedDoctor(capableDoctor);
        patient.setAssignedDoctor(capableDoctor);

        System.out.println("✅ Assigned " + capableDoctor.getName() + " to " + patient.getName());

        return patientRepo.save(patient);
    }
}
