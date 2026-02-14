package com.prm292.techstore.dtos.requests;

import com.prm292.techstore.common.GlobalPattern;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "Username is required.")
    @Size(max = 50, message = "Username must not exceed 50 characters.")
    private String username;

    @NotBlank(message = "Password is required.")
    private String password;

    @NotBlank(message = "Confirm password is required.")
    private String confirmPassword;

    @Email(message = "Invalid email address.")
    @Size(max = 100, message = "Email must not exceed 100 characters.")
    private String email;

    @Pattern(regexp = GlobalPattern.PhonePattern, message = "Invalid phone number.")
    @Size(max = 15, message = "Phone number must not exceed 15 characters.")
    private String phoneNumber;

    @Size(max = 255, message = "Address must not exceed 255 characters.")
    private String address;
}
