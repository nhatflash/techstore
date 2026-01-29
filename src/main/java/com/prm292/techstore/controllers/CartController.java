package com.prm292.techstore.controllers;


import com.prm292.techstore.apis.ApiResponse;
import com.prm292.techstore.dtos.requests.ManageProductToCartRequest;
import com.prm292.techstore.dtos.responses.CartResponse;
import com.prm292.techstore.exceptions.UnauthorizedAccessException;
import com.prm292.techstore.services.CartService;
import com.prm292.techstore.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;


    @PostMapping
    public ResponseEntity<ApiResponse<CartResponse>> AddProductToCart(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid ManageProductToCartRequest request) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("User is not logged in.");
        }
        var response = cartService.HandleAddProductToCart(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> GetUserCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("User is not logged in.");
        }
        var response = cartService.HandleGetUserCart(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.created(response));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<CartResponse>> AdjustProductQuantityInCart(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid ManageProductToCartRequest request) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("User is not logged in.");
        }
        var response = cartService.HandleAdjustProductQuantityInCart(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> RemoveProductFromCart(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable int cartItemId) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("User is not logged in.");
        }
        var response = cartService.HandleRemoveItemFromCart(userDetails.getUsername(), cartItemId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @DeleteMapping
    public ResponseEntity<ApiResponse<CartResponse>> RemoveEntireCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("User is not logged in.");
        }
        var response = cartService.HandleRemoveEntireCart(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
