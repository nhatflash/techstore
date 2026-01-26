package com.prm292.techstore.services;

import com.prm292.techstore.common.constants.UserRole;
import com.prm292.techstore.dtos.responses.SignInResponse;
import com.prm292.techstore.dtos.responses.UserResponse;
import com.prm292.techstore.exceptions.BadRequestException;
import com.prm292.techstore.exceptions.NotFoundException;
import com.prm292.techstore.exceptions.UnauthorizedAccessException;
import com.prm292.techstore.common.mappers.ResponseMapper;
import com.prm292.techstore.models.User;
import com.prm292.techstore.repositories.UserRepository;
import com.prm292.techstore.userdetails.CustomUserDetails;
import com.prm292.techstore.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;

    private final AuthenticationManager authenticationManager;


    public SignInResponse HandleSignIn(String login, String password) {
        String authenticatedUsername = setAuthenticationAndGetUsername(login, password);
        User user = userRepository.findFirstByUsernameIgnoreCase(authenticatedUsername).orElseThrow(() -> new UnauthorizedAccessException("Invalid credentials."));

        String accessToken = jwtUtils.generateAccessToken(user.getUsername(), user.getEmail(), user.getRole());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());

        return ResponseMapper.mapToSignInResponse(accessToken, refreshToken);
    }

    public UserResponse HandleSignUpCustomer(String username, String password, String confirmPassword, String email, String phoneNumber, String address) {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new BadRequestException("Username is already in use");
        }
        if (email != null && userRepository.existsByEmailIgnoreCase(email)) {
            throw new BadRequestException("Email is already in use");
        }
        if (phoneNumber != null && userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new BadRequestException("Phone number is already in use");
        }
        if (!password.equals(confirmPassword)) {
            throw new BadRequestException("Passwords do not match");
        }
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setAddress(address);
        user.setRole(UserRole.Customer);
        userRepository.save(user);
        return ResponseMapper.mapToUserResponse(user);
    }


    public String HandleGetAccessToken(String refreshToken) {
        String username = jwtUtils.getUsernameFromToken(refreshToken);
        User user =  userRepository.findFirstByUsernameIgnoreCase(username).orElseThrow(() -> new NotFoundException("User not found"));
        return jwtUtils.generateAccessToken(user.getUsername(), user.getEmail(), user.getRole());
    }

    private String setAuthenticationAndGetUsername(String login, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        if (customUserDetails == null) {
            throw new UnauthorizedAccessException("User is not authenticated.");
        }
        return customUserDetails.getUsername();
    }
}
