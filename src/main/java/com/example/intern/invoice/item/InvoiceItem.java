package com.example.intern.invoice.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_items")
@Getter
@Setter
@NoArgsConstructor
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @Column(name = "medicine_id")
    private Long medicineId;

    @Column(name = "item_name", nullable = false, length = 150)
    private String itemName;

    @Column(name = "item_type", nullable = false, length = 20)
    private String itemType = "SERVICE";

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "unit_price",
            nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "line_total",
            insertable = false, updatable = false,
            precision = 14, scale = 2)
    private BigDecimal lineTotal;

    @Column(name = "created_at",
            insertable = false, updatable = false)
    private LocalDateTime createdAt;
}