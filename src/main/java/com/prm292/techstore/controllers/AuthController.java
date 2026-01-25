package com.prm292.techstore.controllers;

import com.prm292.techstore.apis.ApiResponse;
import com.prm292.techstore.dtos.requests.SignInRequest;
import com.prm292.techstore.dtos.requests.SignUpRequest;
import com.prm292.techstore.dtos.responses.SignInResponse;
import com.prm292.techstore.dtos.responses.UserResponse;
import com.prm292.techstore.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<SignInResponse>> SignIn(@RequestBody @Valid SignInRequest request) {
        SignInResponse response = authService.HandleSignIn(request.getLogin(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<UserResponse>> SignUpCustomer(@RequestBody @Valid SignUpRequest request) {
        UserResponse response = authService.HandleSignUpCustomer(request.getUsername(), request.getPassword(), request.getConfirmPassword(), request.getEmail(), request.getPhoneNumber(), request.getAddress());
        return ResponseEntity.ok(ApiResponse.created(response));
    }
}
