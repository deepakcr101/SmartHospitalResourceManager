package com.hospital.resource_manager.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.hospital.resource_manager.domain.Equipment;
//import com.sun.tools.javac.util.List;

public interface EquipmentRepository extends Neo4jRepository<Equipment, Long> {

	List<Equipment> findByStatus(String status);
}
