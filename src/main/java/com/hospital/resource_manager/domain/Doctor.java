package com.hospital.resource_manager.domain;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Node("Doctor")
@Data
@EqualsAndHashCode(callSuper = true)
public class Doctor extends BaseEntity {

	private String name;
	private String department;
	private boolean isActive;

	// RELATIONSHIP: Doctor can perform procedure
	// this allows us to query :" Who can perform :Heart Surgery'?"
	@Relationship(type = "CAN_PERFORM", direction = Relationship.Direction.OUTGOING)
	private Set<Procedure> knownProcedures = new HashSet<>();
}
