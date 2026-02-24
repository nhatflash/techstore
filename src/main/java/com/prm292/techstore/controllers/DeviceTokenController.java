package com.prm292.techstore.controllers;

import com.prm292.techstore.apis.ApiResponse;
import com.prm292.techstore.dtos.requests.RegisterDeviceTokenRequest;
import com.prm292.techstore.exceptions.UnauthorizedAccessException;
import com.prm292.techstore.services.DeviceTokenService;
import com.prm292.techstore.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/device-token")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class DeviceTokenController {

    private final DeviceTokenService deviceTokenService;

    @PostMapping
    @PreAuthorize("hasRole('Customer')")
    public ResponseEntity<ApiResponse<Void>> RegisterToken(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid RegisterDeviceTokenRequest request) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("User is not logged in.");
        }
        deviceTokenService.registerToken(userDetails.getUsername(), request.getToken());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('Customer')")
    public ResponseEntity<ApiResponse<Void>> RemoveToken(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid RegisterDeviceTokenRequest request) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("User is not logged in.");
        }
        deviceTokenService.removeToken(userDetails.getUsername(), request.getToken());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
