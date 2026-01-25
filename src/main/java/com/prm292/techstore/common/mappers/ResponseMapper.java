package com.prm292.techstore.common.mappers;

import com.prm292.techstore.dtos.responses.SignInResponse;
import com.prm292.techstore.dtos.responses.UserResponse;
import com.prm292.techstore.models.User;


public class ResponseMapper {


    public static SignInResponse mapToSignInResponse(String accessToken, String refreshToken) {
        return SignInResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .build();
    }
}
