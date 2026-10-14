package com.example.intern.invoice.item.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record InvoiceItemRequest(

        @NotNull(message = "ID hóa đơn không được để trống")
        @Positive(message = "ID hóa đơn phải lớn hơn 0")
        Long invoiceId,

        @Positive(message = "ID thuốc phải lớn hơn 0")
        Long medicineId,

        @NotBlank(message = "Tên khoản thu không được để trống")
        @Size(max = 150, message = "Tên khoản thu tối đa 150 ký tự")
        String itemName,

        @NotBlank(message = "Loại khoản thu không được để trống")
        String itemType,

        @NotNull(message = "Số lượng không được để trống")
        @Positive(message = "Số lượng phải lớn hơn 0")
        Integer quantity,

        @NotNull(message = "Đơn giá không được để trống")
        @DecimalMin(value = "0.00", message = "Đơn giá không được âm")
        BigDecimal unitPrice
) {
}