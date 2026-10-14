package com.example.intern.doctor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository
        extends JpaRepository<Doctor, Long> {

    boolean existsByLicenseNumber(String licenseNumber);

    boolean existsByLicenseNumberAndIdNot(
            String licenseNumber,
            Long id
    );

    Optional<Doctor> findByLicenseNumber(String licenseNumber);
}