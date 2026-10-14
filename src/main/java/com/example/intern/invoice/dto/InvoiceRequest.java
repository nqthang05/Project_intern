package com.example.intern.invoice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record InvoiceRequest(

        @NotNull(message = "ID lịch hẹn không được để trống")
        @Positive(message = "ID lịch hẹn phải lớn hơn 0")
        Long appointmentId,

        @Size(max = 20, message = "Trạng thái tối đa 20 ký tự")
        String status,

        @Size(max = 30, message = "Phương thức thanh toán tối đa 30 ký tự")
        String paymentMethod,

        @Size(max = 255, message = "Ghi chú tối đa 255 ký tự")
        String notes
) {
}