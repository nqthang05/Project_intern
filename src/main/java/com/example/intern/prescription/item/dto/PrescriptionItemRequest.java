package com.example.intern.prescription.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record PrescriptionItemRequest(

        @NotNull(message = "ID đơn thuốc không được để trống")
        @Positive(message = "ID đơn thuốc phải lớn hơn 0")
        Long prescriptionId,

        @NotNull(message = "ID thuốc không được để trống")
        @Positive(message = "ID thuốc phải lớn hơn 0")
        Long medicineId,

        @NotBlank(message = "Liều dùng không được để trống")
        @Size(max = 100, message = "Liều dùng tối đa 100 ký tự")
        String dosage,

        @Size(max = 100, message = "Tần suất tối đa 100 ký tự")
        String frequency,

        @Positive(message = "Số ngày dùng phải lớn hơn 0")
        Integer durationDays,

        @NotNull(message = "Số lượng không được để trống")
        @Positive(message = "Số lượng phải lớn hơn 0")
        Integer quantity,

        @Size(max = 255, message = "Hướng dẫn tối đa 255 ký tự")
        String instructions
) {
}