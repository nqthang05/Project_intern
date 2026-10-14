package com.example.intern.prescription.item.dto;

import java.time.LocalDateTime;

public record PrescriptionItemResponse(
        Long id,
        Long prescriptionId,
        Long medicineId,
        String dosage,
        String frequency,
        Integer durationDays,
        Integer quantity,
        String instructions,
        LocalDateTime createdAt
) {
}