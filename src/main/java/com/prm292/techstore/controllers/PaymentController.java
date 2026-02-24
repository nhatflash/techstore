package com.prm292.techstore.controllers;

import com.prm292.techstore.apis.ApiResponse;
import com.prm292.techstore.dtos.responses.OrderSummaryResponse;
import com.prm292.techstore.exceptions.UnauthorizedAccessException;
import com.prm292.techstore.services.PayOsService;
import com.prm292.techstore.services.PaymentService;
import com.prm292.techstore.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.payos.model.webhooks.Webhook;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final PayOsService payOsService;

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('Customer')")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<String>> getPaymentUrl(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable int orderId, HttpServletRequest request) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("User is not logged in");
        }
        var response = paymentService.handleGetPaymentUrl(userDetails.getUsername(), orderId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/payos-webhook")
    public ResponseEntity<String> payOsWebhook(@RequestBody Webhook webhook) {
        payOsService.handleWebhook(webhook);
        return ResponseEntity.ok("Webhook received");
    }

    @GetMapping("/summary/{paymentId}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<OrderSummaryResponse>> getOrderSummary(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Integer paymentId) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("User is not logged in.");
        }
        OrderSummaryResponse response = paymentService.handleGetOrderSummary(userDetails.getUsername(), paymentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
