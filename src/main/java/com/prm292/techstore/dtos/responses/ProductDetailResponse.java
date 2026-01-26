package com.prm292.techstore.dtos.responses;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ProductDetailResponse(
        Integer id,
        String productName,
        String briefDescription,
        String fullDescription,
        Map<String, Object> technicalSpecifications,
        BigDecimal price,
        String primaryImageUrl,
        List<String> additionalImageUrls,
        CategoryResponse category,
        BrandResponse brand
) {
}
