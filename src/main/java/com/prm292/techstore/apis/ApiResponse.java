package com.prm292.techstore.apis;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {
    private int statusCode;
    private String message;
    private T value;

    public static <T> ApiResponse<T> success(T value) {
        return ApiResponse.<T>builder()
                .statusCode(200)
                .message("Success")
                .value(value)
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T value) {
        return ApiResponse.<T>builder()
                .statusCode(200)
                .message(message)
                .value(value)
                .build();
    }

    public static <T> ApiResponse<T> created(T value) {
        return ApiResponse.<T>builder()
                .statusCode(201)
                .message("Created")
                .value(value)
                .build();
    }

}
