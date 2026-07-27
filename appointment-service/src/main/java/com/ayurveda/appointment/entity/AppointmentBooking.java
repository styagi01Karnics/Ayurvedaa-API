package com.ayurveda.appointment.entity;

import java.time.LocalDate;
import java.util.UUID;

import com.ayurveda.appointment.enums.BookingStatus;
import com.ayurveda.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment_bookings")
public class AppointmentBooking extends BaseEntity {

    @Column(nullable = false)
    private UUID patientId;

    @Column(nullable = false)
    private LocalDate registrationDate;

    @Column(nullable = false)
    private UUID assignedDoctorId;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

}
