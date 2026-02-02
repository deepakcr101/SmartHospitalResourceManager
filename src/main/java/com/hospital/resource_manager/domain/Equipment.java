package com.hospital.resource_manager.domain;

import org.springframework.data.neo4j.core.schema.Node;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Node("Equipment")
@Data
@EqualsAndHashCode(callSuper = true)
public class Equipment extends BaseEntity {

	private String name;
	private String type;
	private String status;
	// AVAILABLE,MAINTENANCE IN_USE

}
