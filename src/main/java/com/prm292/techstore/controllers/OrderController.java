package com.prm292.techstore.controllers;

import com.prm292.techstore.apis.ApiResponse;
import com.prm292.techstore.dtos.requests.CreateOrderRequest;
import com.prm292.techstore.dtos.responses.OrderResponse;
import com.prm292.techstore.exceptions.UnauthorizedAccessException;
import com.prm292.techstore.services.OrderService;
import com.prm292.techstore.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    @PreAuthorize("hasRole('Customer')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid CreateOrderRequest request) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("User is not logged in.");
        }
        var response = orderService.handleCreateOrder(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.created(response));
    }

}
