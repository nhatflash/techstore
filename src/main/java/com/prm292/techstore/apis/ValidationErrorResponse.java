package com.prm292.techstore.apis;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> errors;

    public ValidationErrorResponse(int statusCode, String message, Map<String, String> errors) {
        super(statusCode, message);
        this.errors = errors;
    }
}
