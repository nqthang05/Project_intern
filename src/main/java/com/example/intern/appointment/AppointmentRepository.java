package com.example.intern.appointment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByStatus(String status);

    boolean existsByDoctorIdAndAppointmentTimeAndStatusNotAndIdNot(
            Long doctorId,
            java.time.LocalDateTime appointmentTime,
            String status,
            Long id
    );

    boolean existsByDoctorIdAndAppointmentTimeAndStatusNot(
            Long doctorId,
            java.time.LocalDateTime appointmentTime,
            String status
    );

}