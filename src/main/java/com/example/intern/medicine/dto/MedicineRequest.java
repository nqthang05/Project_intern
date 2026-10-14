package com.example.intern.medicine.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MedicineRequest(

        @NotBlank(message = "Tên thuốc không được để trống")
        @Size(max = 150, message = "Tên thuốc tối đa 150 ký tự")
        String name,

        @Size(max = 150, message = "Hoạt chất tối đa 150 ký tự")
        String activeIngredient,

        @NotBlank(message = "Đơn vị thuốc không được để trống")
        @Size(max = 30, message = "Đơn vị tối đa 30 ký tự")
        String unit,

        String description,

        @NotNull(message = "Giá thuốc không được để trống")
        @DecimalMin(
                value = "0.00",
                message = "Giá thuốc không được âm"
        )
        BigDecimal unitPrice,

        @NotNull(message = "Số lượng tồn kho không được để trống")
        @PositiveOrZero(message = "Số lượng tồn kho không được âm")
        Integer stockQuantity,

        @Pattern(
                regexp = "ACTIVE|INACTIVE",
                message = "Trạng thái phải là ACTIVE hoặc INACTIVE"
        )
        String status
) {
}