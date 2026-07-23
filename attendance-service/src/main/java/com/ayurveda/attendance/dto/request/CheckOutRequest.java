package com.ayurveda.attendance.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CheckOutRequest {

    private LocalDateTime checkOutTime;

}
