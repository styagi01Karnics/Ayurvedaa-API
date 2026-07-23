package com.ayurveda.attendance.dto.request;

import com.ayurveda.attendance.enums.AttendanceStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAttendanceStatusRequest {

    @NotNull(message = "Status is required")
    private AttendanceStatus status;

}
