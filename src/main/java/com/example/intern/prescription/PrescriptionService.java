package com.example.intern.prescription;

import com.example.intern.medicalrecord.MedicalRecordRepository;
import com.example.intern.prescription.dto.PrescriptionRequest;
import com.example.intern.prescription.dto.PrescriptionResponse;
import com.example.intern.prescription.item.PrescriptionItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            MedicalRecordRepository medicalRecordRepository,
            PrescriptionItemRepository prescriptionItemRepository
    ) {
        this.prescriptionRepository = prescriptionRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getAll() {
        return prescriptionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PrescriptionResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getByMedicalRecordId(
            Long medicalRecordId
    ) {
        if (medicalRecordId == null || medicalRecordId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ID hồ sơ khám không hợp lệ"
            );
        }

        if (!medicalRecordRepository.existsById(medicalRecordId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy hồ sơ khám có ID: " + medicalRecordId
            );
        }

        return prescriptionRepository
                .findByMedicalRecordId(medicalRecordId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PrescriptionResponse create(PrescriptionRequest request) {
        validateRequest(request);

        if (!medicalRecordRepository.existsById(
                request.medicalRecordId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy hồ sơ khám có ID: "
                            + request.medicalRecordId()
            );
        }

        Prescription prescription = new Prescription();
        prescription.setMedicalRecordId(request.medicalRecordId());
        prescription.setNotes(trimToNull(request.notes()));

        Prescription saved = prescriptionRepository.saveAndFlush(prescription);

        // Đọc lại để lấy thời gian do database tự sinh.
        return toResponse(findById(saved.getId()));
    }

    @Transactional
    public PrescriptionResponse update(
            Long id,
            PrescriptionRequest request
    ) {
        validateRequest(request);

        Prescription prescription = findById(id);

        if (!medicalRecordRepository.existsById(
                request.medicalRecordId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy hồ sơ khám có ID: "
                            + request.medicalRecordId()
            );
        }

        prescription.setMedicalRecordId(request.medicalRecordId());
        prescription.setNotes(trimToNull(request.notes()));

        return toResponse(prescriptionRepository.save(prescription));
    }

    @Transactional
    public void delete(Long id) {
        Prescription prescription = findById(id);

        if (prescriptionItemRepository.existsByPrescriptionId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Không thể xóa đơn thuốc đã có chi tiết thuốc"
            );
        }

        prescriptionRepository.delete(prescription);
    }

    private Prescription findById(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ID đơn thuốc không hợp lệ"
            );
        }

        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy đơn thuốc có ID: " + id
                ));
    }

    private void validateRequest(PrescriptionRequest request) {
        if (request == null
                || request.medicalRecordId() == null
                || request.medicalRecordId() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ID hồ sơ khám phải lớn hơn 0"
            );
        }
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private PrescriptionResponse toResponse(Prescription prescription) {
        return new PrescriptionResponse(
                prescription.getId(),
                prescription.getMedicalRecordId(),
                prescription.getPrescribedDate(),
                prescription.getNotes(),
                prescription.getCreatedAt()
        );
    }
}