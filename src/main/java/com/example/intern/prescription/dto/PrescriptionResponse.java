package com.example.intern.prescription.dto;

import java.time.LocalDateTime;

public record PrescriptionResponse(
        Long id,
        Long medicalRecordId,
        LocalDateTime prescribedDate,
        String notes,
        LocalDateTime createdAt
) {
}