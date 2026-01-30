package com.prm292.techstore.controllers;

import com.prm292.techstore.apis.ApiResponse;
import com.prm292.techstore.exceptions.UnauthorizedAccessException;
import com.prm292.techstore.services.PaymentService;
import com.prm292.techstore.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('Customer')")
    public ResponseEntity<ApiResponse<String>> getPaymentUrl(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable int orderId, HttpServletRequest request) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("User is not logged in");
        }
        var response = paymentService.handleGetPaymentUrl(userDetails.getUsername(), orderId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
