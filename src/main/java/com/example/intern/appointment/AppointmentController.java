package com.example.intern.appointment;

import com.example.intern.appointment.dto.AppointmentRequest;
import com.example.intern.appointment.dto.AppointmentResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // GET /api/appointments
    @GetMapping
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    // GET /api/appointments/{id}
    @GetMapping("/{id}")
    public AppointmentResponse getAppointmentById(
            @PathVariable Long id
    ) {
        return appointmentService.getAppointmentById(id);
    }

    // GET /api/appointments/patient/{patientId}
    @GetMapping("/patient/{patientId}")
    public List<AppointmentResponse> getAppointmentsByPatient(
            @PathVariable Long patientId
    ) {
        return appointmentService.getAppointmentsByPatient(patientId);
    }

    // GET /api/appointments/doctor/{doctorId}
    @GetMapping("/doctor/{doctorId}")
    public List<AppointmentResponse> getAppointmentsByDoctor(
            @PathVariable Long doctorId
    ) {
        return appointmentService.getAppointmentsByDoctor(doctorId);
    }

    // GET /api/appointments/status/{status}
    @GetMapping("/status/{status}")
    public List<AppointmentResponse> getAppointmentsByStatus(
            @PathVariable String status
    ) {
        return appointmentService.getAppointmentsByStatus(status);
    }

    // POST /api/appointments
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse createAppointment(
            @Valid @RequestBody AppointmentRequest request
    ) {
        return appointmentService.createAppointment(request);
    }

    // PUT /api/appointments/{id}
    @PutMapping("/{id}")
    public AppointmentResponse updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequest request
    ) {
        return appointmentService.updateAppointment(id, request);
    }

    // DELETE /api/appointments/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
    }
}