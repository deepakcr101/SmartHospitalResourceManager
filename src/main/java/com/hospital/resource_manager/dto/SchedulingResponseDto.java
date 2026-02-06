package com.hospital.resource_manager.dto;

import com.hospital.resource_manager.domain.Doctor;
import com.hospital.resource_manager.domain.Patient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchedulingResponseDto {

    private boolean scheduleStatus;
    private Patient scheduledPatient;
    private String message;
    private Doctor assignedDoctor;
    private boolean isSubstitute;


}
