
package com.example.intern.patient;

import com.example.intern.patient.dto.PatientRequest;
import com.example.intern.patient.dto.PatientResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // Chuyen Entity thanh Response DTO
    private PatientResponse toResponse(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getFullName(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getPhone(),
                patient.getEmail(),
                patient.getAddress(),
                patient.getMedicalHistory(),
                patient.getAllergies(),
                patient.getCreatedAt(),
                patient.getUpdatedAt()
        );
    }

    // Tim benh nhan theo ID
    private Patient getPatientOrThrow(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy bệnh nhân có ID: " + id
                ));
    }

    // Gan du lieu tu Request DTO vao Entity
    private void applyRequest(
            Patient patient,
            PatientRequest request) {

        patient.setFullName(request.fullName().trim());
        patient.setDateOfBirth(request.dateOfBirth());

        patient.setGender(
                request.gender() == null
                        ? null
                        : request.gender().toUpperCase(Locale.ROOT)
        );

        patient.setPhone(request.phone());
        patient.setEmail(request.email());
        patient.setAddress(request.address());
        patient.setMedicalHistory(request.medicalHistory());
        patient.setAllergies(request.allergies());
    }

    // Tao benh nhan
    @Transactional
    public PatientResponse create(PatientRequest request) {
        Patient patient = new Patient();

        applyRequest(patient, request);

        Patient saved = patientRepository.saveAndFlush(patient);

        // Doc lai de lay created_at, updated_at do MySQL tao
        return patientRepository.findById(saved.getId())
                .map(this::toResponse)
                .orElseThrow();
    }

    // Lay danh sach benh nhan
    @Transactional(readOnly = true)
    public List<PatientResponse> getAll() {
        return patientRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Lay chi tiet benh nhan
    @Transactional(readOnly = true)
    public PatientResponse getById(Long id) {
        return toResponse(getPatientOrThrow(id));
    }

    // Cap nhat benh nhan
    @Transactional
    public PatientResponse update(
            Long id,
            PatientRequest request) {

        Patient patient = getPatientOrThrow(id);

        applyRequest(patient, request);

        patientRepository.saveAndFlush(patient);

        return patientRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow();
    }

    // Xoa benh nhan
    @Transactional
    public void delete(Long id) {
        Patient patient = getPatientOrThrow(id);
        patientRepository.delete(patient);
        patientRepository.flush();
    }
}
