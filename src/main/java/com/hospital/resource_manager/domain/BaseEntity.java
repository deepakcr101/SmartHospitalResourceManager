package com.hospital.resource_manager.domain;

import java.util.UUID;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;

import lombok.Getter;

//its just a template for other nodes
public abstract class BaseEntity {

	@Id
	@GeneratedValue // auto generate a unique id
	@Getter
	private Long id;

	// also a public uuid for exposed apis to use
	@Getter
	private String publicId = UUID.randomUUID().toString();

	// here @Getter from lombok saves us from writing getPublicId(),getId() etc.
}
