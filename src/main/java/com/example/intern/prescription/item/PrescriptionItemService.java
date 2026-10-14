package com.example.intern.prescription.item;

import com.example.intern.medicine.MedicineRepository;
import com.example.intern.prescription.PrescriptionRepository;
import com.example.intern.prescription.item.dto.PrescriptionItemRequest;
import com.example.intern.prescription.item.dto.PrescriptionItemResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PrescriptionItemService {

    private final PrescriptionItemRepository itemRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MedicineRepository medicineRepository;

    public PrescriptionItemService(
            PrescriptionItemRepository itemRepository,
            PrescriptionRepository prescriptionRepository,
            MedicineRepository medicineRepository
    ) {
        this.itemRepository = itemRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.medicineRepository = medicineRepository;
    }

    @Transactional(readOnly = true)
    public List<PrescriptionItemResponse> getAll() {
        return itemRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PrescriptionItemResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public List<PrescriptionItemResponse> getByPrescriptionId(
            Long prescriptionId
    ) {
        validatePositiveId(prescriptionId, "ID đơn thuốc");

        if (!prescriptionRepository.existsById(prescriptionId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy đơn thuốc có ID: " + prescriptionId
            );
        }

        return itemRepository.findByPrescriptionId(prescriptionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PrescriptionItemResponse create(
            PrescriptionItemRequest request
    ) {
        validateRequest(request);
        checkReferences(request);

        PrescriptionItem item = new PrescriptionItem();
        applyRequest(item, request);

        return toResponse(itemRepository.save(item));
    }

    @Transactional
    public PrescriptionItemResponse update(
            Long id,
            PrescriptionItemRequest request
    ) {
        validateRequest(request);

        PrescriptionItem item = findById(id);
        checkReferences(request);
        applyRequest(item, request);

        return toResponse(itemRepository.save(item));
    }

    @Transactional
    public void delete(Long id) {
        PrescriptionItem item = findById(id);
        itemRepository.delete(item);
    }

    private PrescriptionItem findById(Long id) {
        validatePositiveId(id, "ID chi tiết đơn thuốc");

        return itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy chi tiết đơn thuốc có ID: " + id
                ));
    }

    private void validateRequest(PrescriptionItemRequest request) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Dữ liệu chi tiết đơn thuốc không được để trống"
            );
        }

        validatePositiveId(request.prescriptionId(), "ID đơn thuốc");
        validatePositiveId(request.medicineId(), "ID thuốc");

        if (request.dosage() == null || request.dosage().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Liều dùng không được để trống"
            );
        }

        if (request.quantity() == null || request.quantity() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Số lượng phải lớn hơn 0"
            );
        }

        if (request.durationDays() != null
                && request.durationDays() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Số ngày dùng phải lớn hơn 0"
            );
        }
    }

    private void checkReferences(PrescriptionItemRequest request) {
        if (!prescriptionRepository.existsById(
                request.prescriptionId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy đơn thuốc có ID: "
                            + request.prescriptionId()
            );
        }

        if (!medicineRepository.existsById(request.medicineId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy thuốc có ID: "
                            + request.medicineId()
            );
        }
    }

    private void applyRequest(
            PrescriptionItem item,
            PrescriptionItemRequest request
    ) {
        item.setPrescriptionId(request.prescriptionId());
        item.setMedicineId(request.medicineId());
        item.setDosage(request.dosage().trim());
        item.setFrequency(trimToNull(request.frequency()));
        item.setDurationDays(request.durationDays());
        item.setQuantity(request.quantity());
        item.setInstructions(trimToNull(request.instructions()));
    }

    private void validatePositiveId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    fieldName + " phải lớn hơn 0"
            );
        }
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private PrescriptionItemResponse toResponse(PrescriptionItem item) {
        return new PrescriptionItemResponse(
                item.getId(),
                item.getPrescriptionId(),
                item.getMedicineId(),
                item.getDosage(),
                item.getFrequency(),
                item.getDurationDays(),
                item.getQuantity(),
                item.getInstructions(),
                item.getCreatedAt()
        );
    }
}