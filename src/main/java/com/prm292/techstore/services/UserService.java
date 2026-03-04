package com.prm292.techstore.services;

import org.springframework.stereotype.Service;

import com.prm292.techstore.common.mappers.ResponseMapper;
import com.prm292.techstore.dtos.responses.UserResponse;
import com.prm292.techstore.exceptions.NotFoundException;
import com.prm292.techstore.models.User;
import com.prm292.techstore.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;

    public UserResponse handleGetUserByUsername(String username) {
        User user = userRepository.findFirstByUsernameIgnoreCase(username).orElseThrow(() -> new NotFoundException("User not found"));
        return ResponseMapper.mapToUserResponse(user);
    }
}
