package com.example.intern.medicine;

import com.example.intern.medicine.dto.MedicineRequest;
import com.example.intern.medicine.dto.MedicineResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    // 1. Lấy danh sách thuốc
    @Transactional(readOnly = true)
    public List<MedicineResponse> getAllMedicines() {
        return medicineRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 2. Lấy chi tiết thuốc
    @Transactional(readOnly = true)
    public MedicineResponse getMedicineById(Long id) {
        return toResponse(findMedicineById(id));
    }

    // 3. Tìm thuốc theo tên
    @Transactional(readOnly = true)
    public List<MedicineResponse> searchByName(String name) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tên thuốc tìm kiếm không được để trống"
            );
        }

        return medicineRepository
                .findByNameContainingIgnoreCase(name.trim())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 4. Tìm thuốc theo hoạt chất
    @Transactional(readOnly = true)
    public List<MedicineResponse> searchByActiveIngredient(
            String activeIngredient
    ) {
        if (activeIngredient == null || activeIngredient.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Hoạt chất tìm kiếm không được để trống"
            );
        }

        return medicineRepository
                .findByActiveIngredientContainingIgnoreCase(
                        activeIngredient.trim()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 5. Lọc theo trạng thái
    @Transactional(readOnly = true)
    public List<MedicineResponse> getByStatus(String status) {
        validateStatus(status);

        return medicineRepository.findByStatus(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 6. Thêm thuốc
    @Transactional
    public MedicineResponse createMedicine(MedicineRequest request) {
        validateRequest(request);

        Medicine medicine = new Medicine();
        applyRequest(medicine, request);

        Medicine savedMedicine = medicineRepository.save(medicine);

        return toResponse(savedMedicine);
    }

    // 7. Cập nhật thuốc
    @Transactional
    public MedicineResponse updateMedicine(
            Long id,
            MedicineRequest request
    ) {
        validateRequest(request);

        Medicine medicine = findMedicineById(id);
        applyRequest(medicine, request);

        Medicine savedMedicine = medicineRepository.save(medicine);

        return toResponse(savedMedicine);
    }

    // 8. Xóa thuốc
    @Transactional
    public void deleteMedicine(Long id) {
        Medicine medicine = findMedicineById(id);
        medicineRepository.delete(medicine);
    }

    // Tìm thuốc theo ID
    private Medicine findMedicineById(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ID thuốc không hợp lệ"
            );
        }

        return medicineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy thuốc có ID: " + id
                ));
    }

    // Kiểm tra dữ liệu trước khi lưu
    private void validateRequest(MedicineRequest request) {
        if (request.unitPrice() != null
                && request.unitPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Giá thuốc không được âm"
            );
        }

        if (request.stockQuantity() != null
                && request.stockQuantity() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Số lượng tồn kho không được âm"
            );
        }

        if (request.status() != null && !request.status().isBlank()) {
            validateStatus(request.status());
        }
    }

    // Kiểm tra trạng thái
    private void validateStatus(String status) {
        if (!"ACTIVE".equals(status)
                && !"INACTIVE".equals(status)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Trạng thái thuốc phải là ACTIVE hoặc INACTIVE"
            );
        }
    }

    // Chuyển dữ liệu Request sang Entity
    private void applyRequest(
            Medicine medicine,
            MedicineRequest request
    ) {
        medicine.setName(request.name().trim());
        medicine.setActiveIngredient(
                trimToNull(request.activeIngredient())
        );
        medicine.setUnit(request.unit().trim());
        medicine.setDescription(trimToNull(request.description()));
        medicine.setUnitPrice(request.unitPrice());
        medicine.setStockQuantity(request.stockQuantity());

        String status = request.status();

        if (status == null || status.isBlank()) {
            status = medicine.getId() == null
                    ? "ACTIVE"
                    : medicine.getStatus();
        }

        medicine.setStatus(status);
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    // Chuyển Entity sang Response
    private MedicineResponse toResponse(Medicine medicine) {
        return new MedicineResponse(
                medicine.getId(),
                medicine.getName(),
                medicine.getActiveIngredient(),
                medicine.getUnit(),
                medicine.getDescription(),
                medicine.getUnitPrice(),
                medicine.getStockQuantity(),
                medicine.getStatus(),
                medicine.getCreatedAt(),
                medicine.getUpdatedAt()
        );
    }
}