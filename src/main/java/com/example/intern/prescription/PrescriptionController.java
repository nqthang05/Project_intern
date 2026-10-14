package com.example.intern.prescription;

import com.example.intern.prescription.dto.PrescriptionRequest;
import com.example.intern.prescription.dto.PrescriptionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(
            PrescriptionService prescriptionService
    ) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping
    public List<PrescriptionResponse> getAll() {
        return prescriptionService.getAll();
    }

    @GetMapping("/{id}")
    public PrescriptionResponse getById(@PathVariable Long id) {
        return prescriptionService.getById(id);
    }

    @GetMapping("/medical-record/{medicalRecordId}")
    public List<PrescriptionResponse> getByMedicalRecordId(
            @PathVariable Long medicalRecordId
    ) {
        return prescriptionService.getByMedicalRecordId(medicalRecordId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrescriptionResponse create(
            @Valid @RequestBody PrescriptionRequest request
    ) {
        return prescriptionService.create(request);
    }

    @PutMapping("/{id}")
    public PrescriptionResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PrescriptionRequest request
    ) {
        return prescriptionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        prescriptionService.delete(id);
    }
}