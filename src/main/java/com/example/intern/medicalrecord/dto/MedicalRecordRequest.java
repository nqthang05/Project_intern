package com.example.intern.medicalrecord.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MedicalRecordRequest(

        @NotNull(message = "ID lịch hẹn không được để trống")
        @Positive(message = "ID lịch hẹn phải lớn hơn 0")
        Long appointmentId,

        String symptoms,

        String diagnosis,

        String treatmentPlan,

        String doctorNotes
) {
}