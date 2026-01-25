package com.prm292.techstore.dtos.requests;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenRequest {

    @NotBlank(message = "Refresh token is required.")
    private String refreshToken;
}
