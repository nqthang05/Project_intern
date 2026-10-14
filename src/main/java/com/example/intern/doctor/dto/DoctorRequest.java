package com.example.intern.doctor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DoctorRequest(

        Long userId,

        @NotBlank(message = "Họ tên bác sĩ không được để trống")
        @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
        String fullName,

        @Size(max = 100, message = "Chuyên khoa tối đa 100 ký tự")
        String specialty,

        @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
        String phone,

        @Size(max = 100, message = "Email tối đa 100 ký tự")
        @Pattern(
                regexp = "^[\\w.!#$%&'*+/=?^`{|}~-]+@[\\w-]+(\\.[\\w-]+)+$|^$",
                message = "Email không hợp lệ"
        )
        String email,

        @Size(max = 50, message = "Số giấy phép tối đa 50 ký tự")
        String licenseNumber,

        @Pattern(
                regexp = "ACTIVE|INACTIVE",
                message = "Trạng thái phải là ACTIVE hoặc INACTIVE"
        )
        String status
) {
}