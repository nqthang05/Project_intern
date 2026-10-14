package com.example.intern.medicalrecord.dto;

import java.time.LocalDateTime;

public record MedicalRecordResponse(
        Long id,
        Long appointmentId,
        String symptoms,
        String diagnosis,
        String treatmentPlan,
        String doctorNotes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}