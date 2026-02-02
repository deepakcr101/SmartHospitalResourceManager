package com.hospital.resource_manager.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.hospital.resource_manager.domain.Procedure;

public interface ProcedureRepository extends Neo4jRepository<Procedure, Long> {

}
