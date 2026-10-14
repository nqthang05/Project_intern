package com.example.intern.prescription.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PrescriptionRequest(

        @NotNull(message = "ID hồ sơ khám không được để trống")
        @Positive(message = "ID hồ sơ khám phải lớn hơn 0")
        Long medicalRecordId,

        String notes
) {
}