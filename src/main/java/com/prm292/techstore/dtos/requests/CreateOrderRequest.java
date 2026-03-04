package com.prm292.techstore.dtos.requests;

import org.hibernate.validator.constraints.Length;

import com.prm292.techstore.common.GlobalPattern;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    @NotNull(message = "Cart Id is required")
    private Integer cartId;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @NotBlank(message = "Billing full name is required")
    @Length(max = 256, message = "Billing full name must not exceed 256 characters")
    private String billingFullName;

    @NotBlank(message = "Billing phone is required")
    @Pattern(regexp = GlobalPattern.PhonePattern, message = "Invalid phone number.")
    @Length(max = 15, message = "Billing phone must not exceed 15 characters")
    private String billingPhone;

    @NotBlank(message = "Billing address is required")
    @Length(max = 256, message = "Billing address must not exceed 256 characters")
    private String billingAddress;
}
