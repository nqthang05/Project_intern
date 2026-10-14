package com.example.intern.invoice.item.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvoiceItemResponse(
        Long id,
        Long invoiceId,
        Long medicineId,
        String itemName,
        String itemType,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal,
        LocalDateTime createdAt
) {
}