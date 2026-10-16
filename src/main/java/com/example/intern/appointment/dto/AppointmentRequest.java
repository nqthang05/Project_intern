
package com.example.intern.appointment.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AppointmentRequest(

        @NotNull(message = "ID bệnh nhân không được để trống")
        Long patientId,

        @NotNull(message = "ID bác sĩ không được để trống")
        Long doctorId,

        @NotNull(message = "Thời gian khám không được để trống")
        @Future(message = "Thời gian khám phải ở tương lai")
        LocalDateTime appointmentTime,

        @Size(max = 255, message = "Lý do khám tối đa 255 ký tự")
        String reason,

        @Pattern(
                regexp = "PENDING|CONFIRMED|CANCELLED|COMPLETED",
                message = "Trạng thái không hợp lệ"
        )
        String status,

        String note
) {
}
