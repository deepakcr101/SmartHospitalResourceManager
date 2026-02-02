package com.hospital.resource_manager.domain;

import org.springframework.data.neo4j.core.schema.Node;

import lombok.Data;

@Node("Procedure")
@Data
@EqualAndHashCode(callSuper = true)
public class Procedure {

}
