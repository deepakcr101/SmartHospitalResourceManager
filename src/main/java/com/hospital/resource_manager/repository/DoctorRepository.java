package com.hospital.resource_manager.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.hospital.resource_manager.domain.Doctor;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DoctorRepository extends Neo4jRepository<Doctor, Long> {
   @Query("MATCH (d:Doctor)-[:CAN_PERFORM]->(p:Procedure) "
           +
           "WHERE p.name= $procedureName AND d.isActive = true " +
           "RETURN d LIMIT 1")
   Optional<Doctor> findAvailableDoctorForProcedure(@Param("procedureName")String procedureName);

   @Query("MATCH (d:Doctor)-[:CAN_PERFORM]->(p:Procedure) "
           + "WHERE p.name = $procedureName AND d.isActive = true AND d.id <> $excludeDoctorId "
           + "RETURN d LIMIT 1")
   Optional<Doctor> findSubstituteDoctor(@Param("procedureName") String procedureName, 
                                         @Param("excludeDoctorId") Long excludeDoctorId);
}
