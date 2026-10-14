package com.example.intern.medicine.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MedicineResponse(
        Long id,
        String name,
        String activeIngredient,
        String unit,
        String description,
        BigDecimal unitPrice,
        Integer stockQuantity,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}