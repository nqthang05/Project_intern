package com.example.intern.doctor.dto;

import java.time.LocalDateTime;

public record DoctorResponse(
        Long id,
        Long userId,
        String fullName,
        String specialty,
        String phone,
        String email,
        String licenseNumber,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}