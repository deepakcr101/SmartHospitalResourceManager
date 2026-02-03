package com.hospital.resource_manager.repository;

import com.hospital.resource_manager.domain.Patient;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends Neo4jRepository<Patient, Long> {
}
