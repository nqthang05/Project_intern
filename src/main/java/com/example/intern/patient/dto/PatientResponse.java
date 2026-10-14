
package com.example.intern.patient.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PatientResponse(
        Long id,
        String fullName,
        LocalDate dateOfBirth,
        String gender,
        String phone,
        String email,
        String address,
        String medicalHistory,
        String allergies,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
