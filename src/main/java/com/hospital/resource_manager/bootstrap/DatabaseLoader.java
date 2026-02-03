package com.hospital.resource_manager.bootstrap;

import com.hospital.resource_manager.domain.Doctor;
import com.hospital.resource_manager.domain.Equipment;
import com.hospital.resource_manager.domain.Procedure;
import com.hospital.resource_manager.repository.DoctorRepository;
import com.hospital.resource_manager.repository.EquipmentRepository;
import com.hospital.resource_manager.repository.ProcedureRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DatabaseLoader {

    @Bean
    CommandLineRunner initDatabase(DoctorRepository doctorRepo,
                                   ProcedureRepository procedureRepo,
                                   EquipmentRepository equipmentRepo) {
        return args -> {
            // 1. CLEAR DATABASE (Start Fresh)
            System.out.println("🧹 Cleaning Database...");
            doctorRepo.deleteAll();
            procedureRepo.deleteAll();
            equipmentRepo.deleteAll();

            // 2. CREATE EQUIPMENT (The "Assets")
            Equipment mri = new Equipment();
            mri.setName("MRI Scanner 01");
            mri.setType("Imaging");
            mri.setStatus("AVAILABLE");

            Equipment scalpel = new Equipment();
            scalpel.setName("Surgical Set A");
            scalpel.setType("Surgical");
            scalpel.setStatus("AVAILABLE");

            // Save them first so they have IDs
            equipmentRepo.saveAll(Arrays.asList(mri, scalpel));

            // 3. CREATE PROCEDURES (The "Demand")
            Procedure heartSurgery = new Procedure();
            heartSurgery.setName("Open Heart Surgery");
            heartSurgery.setDurationMinutes(240);
            heartSurgery.getRequiredEquipment().add(scalpel); // Needs Surgical Set

            Procedure brainScan = new Procedure();
            brainScan.setName("Full Brain MRI");
            brainScan.setDurationMinutes(45);
            brainScan.getRequiredEquipment().add(mri); // Needs MRI

            // Save procedures (Cascading isn't always automatic depending on config, better to be explicit)
            procedureRepo.saveAll(Arrays.asList(heartSurgery, brainScan));

            // 4. CREATE DOCTORS (The "Talent")
            Doctor drStrange = new Doctor();
            drStrange.setName("Dr. Stephen Strange");
            drStrange.setDepartment("Neurology");
            drStrange.setActive(true);
            // He can do Brain Scans
            drStrange.getKnownProcedures().add(brainScan);

            Doctor drHouse = new Doctor();
            drHouse.setName("Dr. Gregory House");
            drHouse.setDepartment("Diagnostics");
            drHouse.setActive(true);
            // He can do Heart Surgery AND Brain Scans
            drHouse.getKnownProcedures().add(heartSurgery);
            drHouse.getKnownProcedures().add(brainScan);

            // 5. SAVE EVERYTHING (The "Big Bang")
            doctorRepo.saveAll(Arrays.asList(drStrange, drHouse));

            System.out.println("✅ Database Seeded with Graph Data!");
        };
    }
}