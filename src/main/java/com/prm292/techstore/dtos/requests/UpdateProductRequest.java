package com.prm292.techstore.dtos.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record UpdateProductRequest(
        @Size(max = 100, message = "Product name must be less than 100 characters")
        String productName,

        @Size(max = 500, message = "Brief description must be less than 500 characters")
        String briefDescription,

        String fullDescription,

        Map<String, Object> technicalSpecifications,

        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,


        String primaryImageUrl,

        List<String> additionalImageUrls,

        String categoryName,

        String brandName
) {
}
