package com.example.intern.appointment;

import com.example.intern.appointment.dto.AppointmentRequest;
import com.example.intern.appointment.dto.AppointmentResponse;
import com.example.intern.doctor.DoctorRepository;
import com.example.intern.patient.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    // 1. Lấy danh sách tất cả lịch hẹn
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 2. Lấy chi tiết một lịch hẹn
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        return toResponse(findAppointmentById(id));
    }

    // 3. Lấy lịch hẹn theo bệnh nhân
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByPatient(
            Long patientId
    ) {
        checkPatientExists(patientId);

        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 4. Lấy lịch hẹn theo bác sĩ
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByDoctor(
            Long doctorId
    ) {
        checkDoctorExists(doctorId);

        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 5. Lọc lịch hẹn theo trạng thái
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByStatus(
            String status
    ) {
        validateStatus(status);

        return appointmentRepository.findByStatus(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 6. Tạo lịch hẹn
    @Transactional
    public AppointmentResponse createAppointment(
            AppointmentRequest request
    ) {
        checkPatientExists(request.patientId());
        checkDoctorExists(request.doctorId());

        Appointment appointment = new Appointment();
        applyRequest(appointment, request);

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        return toResponse(savedAppointment);
    }

    // 7. Cập nhật lịch hẹn
    @Transactional
    public AppointmentResponse updateAppointment(
            Long id,
            AppointmentRequest request
    ) {
        Appointment appointment = findAppointmentById(id);

        checkPatientExists(request.patientId());
        checkDoctorExists(request.doctorId());

        applyRequest(appointment, request);

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        return toResponse(savedAppointment);
    }

    // 8. Xóa lịch hẹn
    @Transactional
    public void deleteAppointment(Long id) {
        Appointment appointment = findAppointmentById(id);
        appointmentRepository.delete(appointment);
    }

    // Tìm lịch hẹn theo ID
    private Appointment findAppointmentById(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ID lịch hẹn không hợp lệ"
            );
        }

        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy lịch hẹn có ID: " + id
                ));
    }

    // Kiểm tra bệnh nhân
    private void checkPatientExists(Long patientId) {
        if (patientId == null || patientId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ID bệnh nhân không hợp lệ"
            );
        }

        if (!patientRepository.existsById(patientId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy bệnh nhân có ID: " + patientId
            );
        }
    }

    // Kiểm tra bác sĩ
    private void checkDoctorExists(Long doctorId) {
        if (doctorId == null || doctorId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ID bác sĩ không hợp lệ"
            );
        }

        if (!doctorRepository.existsById(doctorId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy bác sĩ có ID: " + doctorId
            );
        }
    }

    // Kiểm tra trạng thái hợp lệ
    private void validateStatus(String status) {
        if (status == null || !List.of(
                "PENDING",
                "CONFIRMED",
                "CANCELLED",
                "COMPLETED"
        ).contains(status)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Trạng thái lịch hẹn không hợp lệ"
            );
        }
    }

    // Chuyển dữ liệu Request sang Entity
    private void applyRequest(
            Appointment appointment,
            AppointmentRequest request
    ) {
        appointment.setPatientId(request.patientId());
        appointment.setDoctorId(request.doctorId());
        appointment.setAppointmentTime(request.appointmentTime());
        appointment.setReason(trimToNull(request.reason()));
        appointment.setNote(trimToNull(request.note()));

        String status = request.status();

        if (status == null || status.isBlank()) {
            status = appointment.getId() == null
                    ? "PENDING"
                    : appointment.getStatus();
        }

        validateStatus(status);
        appointment.setStatus(status);
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    // Chuyển Entity sang Response
    private AppointmentResponse toResponse(
            Appointment appointment
    ) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatientId(),
                appointment.getDoctorId(),
                appointment.getAppointmentTime(),
                appointment.getReason(),
                appointment.getStatus(),
                appointment.getNote(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }
}