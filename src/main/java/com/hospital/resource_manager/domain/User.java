package com.hospital.resource_manager.domain;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.core.schema.Node;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Node("User")
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    private String username;
    private String password;

    // Storing roles as simple strings for now, e.g., "ROLE_ADMIN", "ROLE_STAFF"
    private Set<String> roles = new HashSet<>();

    public void addRole(String role) {
        this.roles.add(role);
    }
}
