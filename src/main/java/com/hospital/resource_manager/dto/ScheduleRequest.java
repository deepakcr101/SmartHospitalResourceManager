package com.hospital.resource_manager.dto;

import lombok.Data;

@Data
public class ScheduleRequest {
    private String patientName;
    private String procedureName;
    private String priority;

}
