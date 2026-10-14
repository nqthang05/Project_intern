package com.example.intern.appointment.dto;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long patientId,
        Long doctorId,
        LocalDateTime appointmentTime,
        String reason,
        String status,
        String note,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}