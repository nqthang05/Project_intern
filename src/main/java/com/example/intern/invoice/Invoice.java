package com.example.intern.invoice;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "invoices",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_invoices_appointment",
                columnNames = "appointment_id"
        )
)
@Getter
@Setter
@NoArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_id", nullable = false)
    private Long appointmentId;

    @Column(name = "invoice_date",
            insertable = false, updatable = false)
    private LocalDateTime invoiceDate;

    @Column(name = "total_amount",
            nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    private String status = "UNPAID";

    @Column(name = "payment_method", length = 30)
    private String paymentMethod;

    @Column(length = 255)
    private String notes;

    @Column(name = "created_at",
            insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at",
            insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}