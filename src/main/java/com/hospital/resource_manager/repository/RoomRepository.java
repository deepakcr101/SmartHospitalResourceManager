package com.hospital.resource_manager.repository;


import com.hospital.resource_manager.domain.Room;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoomRepository extends Neo4jRepository<Room, Long> {

    // "Find me a Room node 'r'..."
    // "Where 'r' is NOT occupied..."
    // "AND there is a relationship SUITABLE_FOR pointing to a Procedure with the specific name"
    // "LIMIT 1 (Just give me the first one you find)"
    @Query("MATCH (r:Room)-[:SUITABLE_FOR]->(p:Procedure) " +
            "WHERE p.name = $procedureName AND r.isOccupied = false " +
            "RETURN r LIMIT 1")
    Optional<Room> findFirstAvailableRoom(@Param("procedureName") String procedureName);
}
