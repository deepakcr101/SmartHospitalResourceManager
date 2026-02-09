package com.hospital.resource_manager.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
