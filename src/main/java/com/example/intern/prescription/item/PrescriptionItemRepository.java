package com.example.intern.prescription.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionItemRepository
        extends JpaRepository<PrescriptionItem, Long> {

    List<PrescriptionItem> findByPrescriptionId(Long prescriptionId);

    boolean existsByPrescriptionId(Long prescriptionId);

    boolean existsByMedicineId(Long medicineId);
}