
package com.example.intern.patient;

import com.example.intern.patient.dto.PatientRequest;
import com.example.intern.patient.dto.PatientResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // POST /api/patients
    @PostMapping
    public ResponseEntity<PatientResponse> create(
            @Valid @RequestBody PatientRequest request) {

        PatientResponse response = patientService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET /api/patients
    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAll() {
        return ResponseEntity.ok(patientService.getAll());
    }

    // GET /api/patients/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(patientService.getById(id));
    }

    // PUT /api/patients/{id}
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest request) {

        return ResponseEntity.ok(
                patientService.update(id, request)
        );
    }

    // DELETE /api/patients/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        patientService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
