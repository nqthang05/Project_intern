package com.example.intern.medicalrecord;

import com.example.intern.medicalrecord.dto.MedicalRecordRequest;
import com.example.intern.medicalrecord.dto.MedicalRecordResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(
            MedicalRecordService medicalRecordService
    ) {
        this.medicalRecordService = medicalRecordService;
    }

    // GET /api/medical-records
    @GetMapping
    public List<MedicalRecordResponse> getAllMedicalRecords() {
        return medicalRecordService.getAllMedicalRecords();
    }

    // GET /api/medical-records/{id}
    @GetMapping("/{id}")
    public MedicalRecordResponse getMedicalRecordById(
            @PathVariable Long id
    ) {
        return medicalRecordService.getMedicalRecordById(id);
    }

    // GET /api/medical-records/appointment/{appointmentId}
    @GetMapping("/appointment/{appointmentId}")
    public MedicalRecordResponse getByAppointmentId(
            @PathVariable Long appointmentId
    ) {
        return medicalRecordService.getByAppointmentId(appointmentId);
    }

    // POST /api/medical-records
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalRecordResponse createMedicalRecord(
            @Valid @RequestBody MedicalRecordRequest request
    ) {
        return medicalRecordService.createMedicalRecord(request);
    }

    // PUT /api/medical-records/{id}
    @PutMapping("/{id}")
    public MedicalRecordResponse updateMedicalRecord(
            @PathVariable Long id,
            @Valid @RequestBody MedicalRecordRequest request
    ) {
        return medicalRecordService.updateMedicalRecord(id, request);
    }

    // DELETE /api/medical-records/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMedicalRecord(@PathVariable Long id) {
        medicalRecordService.deleteMedicalRecord(id);
    }
}