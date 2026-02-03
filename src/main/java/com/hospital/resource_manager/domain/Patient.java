package com.hospital.resource_manager.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("Patient")
@Data
@EqualsAndHashCode(callSuper=true)
public class Patient extends BaseEntity {
    private String name;
    private String priority;

    //result of successful schedule
    @Relationship(type="SCHEDULED_FOR", direction=Relationship.Direction.OUTGOING)
    private Set<Procedure> scheduledProcedures = new HashSet<>();


    //methods for proper functioning
    //linking the patient with specific doctor assigned
    @Relationship(type="ASSIGNED_DOCTOR", direction= Relationship.Direction.OUTGOING)
    private Doctor assignedDoctor;

}
