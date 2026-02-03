package com.hospital.resource_manager.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("Room")
@Data
@EqualsAndHashCode(callSuper = true)
public class Room extends BaseEntity {

    private String name;         // e.g., "OT-101"
    private String type;         // "ICU", "Operating Theatre", "Ward"
    private boolean isOccupied;  // Simple status for now

    // RELATIONSHIP: Room SUITABLE_FOR Procedure
    // This lets us query: "Which rooms can handle Heart Surgery?"
    @Relationship(type = "SUITABLE_FOR", direction = Relationship.Direction.OUTGOING)
    private Set<Procedure> suitableProcedures = new HashSet<>();

    // Helper to add suitability easily
    public void addSuitability(Procedure procedure) {
        this.suitableProcedures.add(procedure);
    }
}
