package com.example.intern.invoice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvoiceResponse(
        Long id,
        Long appointmentId,
        LocalDateTime invoiceDate,
        BigDecimal totalAmount,
        String status,
        String paymentMethod,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}