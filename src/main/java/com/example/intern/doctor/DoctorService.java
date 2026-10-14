package com.example.intern.doctor;

import com.example.intern.doctor.dto.DoctorRequest;
import com.example.intern.doctor.dto.DoctorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    // 1. Lấy danh sách bác sĩ
    @Transactional(readOnly = true)
    public List<DoctorResponse> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 2. Lấy thông tin một bác sĩ
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(Long id) {
        return toResponse(findDoctorById(id));
    }

    // 3. Thêm bác sĩ
    @Transactional
    public DoctorResponse createDoctor(DoctorRequest request) {
        Doctor doctor = new Doctor();

        applyRequest(doctor, request);

        if (doctor.getLicenseNumber() != null
                && doctorRepository.existsByLicenseNumber(
                doctor.getLicenseNumber())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Số giấy phép hành nghề đã tồn tại"
            );
        }

        Doctor savedDoctor = doctorRepository.save(doctor);

        return toResponse(savedDoctor);
    }

    // 4. Cập nhật bác sĩ
    @Transactional
    public DoctorResponse updateDoctor(
            Long id,
            DoctorRequest request
    ) {
        Doctor doctor = findDoctorById(id);

        applyRequest(doctor, request);

        if (doctor.getLicenseNumber() != null
                && doctorRepository.existsByLicenseNumberAndIdNot(
                doctor.getLicenseNumber(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Số giấy phép hành nghề đã thuộc về bác sĩ khác"
            );
        }

        Doctor savedDoctor = doctorRepository.save(doctor);

        return toResponse(savedDoctor);
    }

    // 5. Xóa bác sĩ
    @Transactional
    public void deleteDoctor(Long id) {
        Doctor doctor = findDoctorById(id);
        doctorRepository.delete(doctor);
    }

    // Tìm bác sĩ theo ID
    private Doctor findDoctorById(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ID bác sĩ không hợp lệ"
            );
        }

        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy bác sĩ có ID: " + id
                ));
    }

    // Sao chép dữ liệu Request vào Entity
    private void applyRequest(
            Doctor doctor,
            DoctorRequest request
    ) {
        doctor.setUserId(request.userId());
        doctor.setFullName(request.fullName().trim());
        doctor.setSpecialty(trimToNull(request.specialty()));
        doctor.setPhone(trimToNull(request.phone()));
        doctor.setEmail(trimToNull(request.email()));
        doctor.setLicenseNumber(
                trimToNull(request.licenseNumber())
        );

        String status = trimToNull(request.status());
        doctor.setStatus(status == null ? "ACTIVE" : status);
    }

    // Chuỗi trống chuyển thành null
    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    // Chuyển Entity thành Response
    private DoctorResponse toResponse(Doctor doctor) {
        return new DoctorResponse(
                doctor.getId(),
                doctor.getUserId(),
                doctor.getFullName(),
                doctor.getSpecialty(),
                doctor.getPhone(),
                doctor.getEmail(),
                doctor.getLicenseNumber(),
                doctor.getStatus(),
                doctor.getCreatedAt(),
                doctor.getUpdatedAt()
        );
    }
}