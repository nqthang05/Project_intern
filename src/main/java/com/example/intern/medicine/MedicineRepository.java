package com.example.intern.medicine;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRepository
        extends JpaRepository<Medicine, Long> {

    List<Medicine> findByStatus(String status);

    List<Medicine> findByNameContainingIgnoreCase(String name);

    List<Medicine> findByActiveIngredientContainingIgnoreCase(
            String activeIngredient
    );
}