package com.prm292.techstore.dtos.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record CreateProductRequest(
        @NotBlank(message = "Product name is required")
        @Size(max = 100, message = "Product name must be less than 100 characters")
        String productName,

        @Size(max = 500, message = "Brief description must be less than 500 characters")
        String briefDescription,

        String fullDescription,

        Map<String, Object> technicalSpecifications,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,

        String primaryImageUrl,

        List<String> additionalImageUrls,

        @NotBlank(message = "Category name is required")
        String categoryName,

        @NotBlank(message = "Brand name is required")
        String brandName
) {
}
