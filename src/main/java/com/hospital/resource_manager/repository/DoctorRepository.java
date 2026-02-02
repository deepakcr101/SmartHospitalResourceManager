package com.hospital.resource_manager.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.hospital.resource_manager.domain.Doctor;

public interface DoctorRepository extends Neo4jRepository<Doctor, Long> {

}
