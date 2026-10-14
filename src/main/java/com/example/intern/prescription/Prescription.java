package com.example.intern.prescription;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
@Getter
@Setter
@NoArgsConstructor
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medical_record_id", nullable = false)
    private Long medicalRecordId;

    @Column(name = "prescribed_date",
            insertable = false, updatable = false)
    private LocalDateTime prescribedDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at",
            insertable = false, updatable = false)
    private LocalDateTime createdAt;

}