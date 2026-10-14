
package com.example.intern.patient.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientRequest(

        @NotBlank(message = "Họ tên không được để trống")
        @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
        String fullName,

        @PastOrPresent(message = "Ngày sinh không được ở tương lai")
        LocalDate dateOfBirth,

        @Pattern(
                regexp = "(?i)MALE|FEMALE|OTHER",
                message = "Giới tính phải là MALE, FEMALE hoặc OTHER"
        )
        String gender,

        @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
        String phone,

        @Email(message = "Email không đúng định dạng")
        @Size(max = 100, message = "Email tối đa 100 ký tự")
        String email,

        @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
        String address,

        String medicalHistory,

        String allergies

) {}
