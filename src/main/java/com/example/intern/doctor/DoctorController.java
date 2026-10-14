package com.example.intern.doctor;

import com.example.intern.doctor.dto.DoctorRequest;
import com.example.intern.doctor.dto.DoctorResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    // GET /api/doctors
    @GetMapping
    public List<DoctorResponse> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    // GET /api/doctors/{id}
    @GetMapping("/{id}")
    public DoctorResponse getDoctorById(
            @PathVariable Long id
    ) {
        return doctorService.getDoctorById(id);
    }

    // POST /api/doctors
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorResponse createDoctor(
            @Valid @RequestBody DoctorRequest request
    ) {
        return doctorService.createDoctor(request);
    }

    // PUT /api/doctors/{id}
    @PutMapping("/{id}")
    public DoctorResponse updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorRequest request
    ) {
        return doctorService.updateDoctor(id, request);
    }

    // DELETE /api/doctors/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
    }
}