package com.hospital.resource_manager.domain;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Node("Procedure")
@Data
@EqualsAndHashCode(callSuper = true)
public class Procedure extends BaseEntity {

	private String name;
	private int durationMinutes;

	// RELATIONSHIP: Procedure REQUIRES Equipment
	// "OUTGOING" means the arrow points FROM Procedure to Equipment
	@Relationship(type = "REQUIRES", direction = Relationship.Direction.OUTGOING)
	private Set<Equipment> requiredEquipment = new HashSet<>();
}
