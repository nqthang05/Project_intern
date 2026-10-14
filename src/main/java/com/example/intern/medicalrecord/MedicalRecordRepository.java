package com.example.intern.medicalrecord;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalRecordRepository
        extends JpaRepository<MedicalRecord, Long> {

    Optional<MedicalRecord> findByAppointmentId(Long appointmentId);

    boolean existsByAppointmentId(Long appointmentId);

    boolean existsByAppointmentIdAndIdNot(
            Long appointmentId,
            Long id
    );
}