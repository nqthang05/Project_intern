package com.example.intern.medicalrecord;

import com.example.intern.appointment.AppointmentRepository;
import com.example.intern.medicalrecord.dto.MedicalRecordRequest;
import com.example.intern.medicalrecord.dto.MedicalRecordResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;

    public MedicalRecordService(
            MedicalRecordRepository medicalRecordRepository,
            AppointmentRepository appointmentRepository
    ) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.appointmentRepository = appointmentRepository;
    }

    // 1. Lấy tất cả hồ sơ khám
    @Transactional(readOnly = true)
    public List<MedicalRecordResponse> getAllMedicalRecords() {
        return medicalRecordRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 2. Lấy hồ sơ theo ID
    @Transactional(readOnly = true)
    public MedicalRecordResponse getMedicalRecordById(Long id) {
        return toResponse(findMedicalRecordById(id));
    }

    // 3. Lấy hồ sơ theo ID lịch hẹn
    @Transactional(readOnly = true)
    public MedicalRecordResponse getByAppointmentId(
            Long appointmentId
    ) {
        validateAppointmentId(appointmentId);

        return medicalRecordRepository
                .findByAppointmentId(appointmentId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Chưa có hồ sơ khám cho lịch hẹn ID: "
                                + appointmentId
                ));
    }

    // 4. Tạo hồ sơ khám
    @Transactional
    public MedicalRecordResponse createMedicalRecord(
            MedicalRecordRequest request
    ) {
        validateAppointmentId(request.appointmentId());

        checkAppointmentExists(request.appointmentId());

        if (medicalRecordRepository.existsByAppointmentId(
                request.appointmentId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Lịch hẹn này đã có hồ sơ khám"
            );
        }

        MedicalRecord medicalRecord = new MedicalRecord();
        applyRequest(medicalRecord, request);

        MedicalRecord savedRecord =
                medicalRecordRepository.save(medicalRecord);

        return toResponse(savedRecord);
    }

    // 5. Cập nhật hồ sơ khám
    @Transactional
    public MedicalRecordResponse updateMedicalRecord(
            Long id,
            MedicalRecordRequest request
    ) {
        MedicalRecord medicalRecord = findMedicalRecordById(id);

        validateAppointmentId(request.appointmentId());
        checkAppointmentExists(request.appointmentId());

        if (medicalRecordRepository
                .existsByAppointmentIdAndIdNot(
                        request.appointmentId(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Lịch hẹn này đã được hồ sơ khác sử dụng"
            );
        }

        applyRequest(medicalRecord, request);

        MedicalRecord savedRecord =
                medicalRecordRepository.save(medicalRecord);

        return toResponse(savedRecord);
    }

    // 6. Xóa hồ sơ khám
    @Transactional
    public void deleteMedicalRecord(Long id) {
        MedicalRecord medicalRecord = findMedicalRecordById(id);
        medicalRecordRepository.delete(medicalRecord);
    }

    // Tìm hồ sơ theo ID
    private MedicalRecord findMedicalRecordById(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ID hồ sơ khám không hợp lệ"
            );
        }

        return medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy hồ sơ khám có ID: " + id
                ));
    }

    // Kiểm tra ID lịch hẹn
    private void validateAppointmentId(Long appointmentId) {
        if (appointmentId == null || appointmentId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ID lịch hẹn phải lớn hơn 0"
            );
        }
    }

    // Kiểm tra lịch hẹn tồn tại
    private void checkAppointmentExists(Long appointmentId) {
        if (!appointmentRepository.existsById(appointmentId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy lịch hẹn có ID: " + appointmentId
            );
        }
    }

    // Chuyển dữ liệu Request vào Entity
    private void applyRequest(
            MedicalRecord medicalRecord,
            MedicalRecordRequest request
    ) {
        medicalRecord.setAppointmentId(request.appointmentId());
        medicalRecord.setSymptoms(trimToNull(request.symptoms()));
        medicalRecord.setDiagnosis(trimToNull(request.diagnosis()));
        medicalRecord.setTreatmentPlan(
                trimToNull(request.treatmentPlan())
        );
        medicalRecord.setDoctorNotes(
                trimToNull(request.doctorNotes())
        );
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    // Chuyển Entity thành Response
    private MedicalRecordResponse toResponse(
            MedicalRecord medicalRecord
    ) {
        return new MedicalRecordResponse(
                medicalRecord.getId(),
                medicalRecord.getAppointmentId(),
                medicalRecord.getSymptoms(),
                medicalRecord.getDiagnosis(),
                medicalRecord.getTreatmentPlan(),
                medicalRecord.getDoctorNotes(),
                medicalRecord.getCreatedAt(),
                medicalRecord.getUpdatedAt()
        );
    }
}