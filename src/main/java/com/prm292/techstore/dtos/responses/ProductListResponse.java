package com.prm292.techstore.dtos.responses;

import java.math.BigDecimal;

public record ProductListResponse(
        Integer id,
        String productName,
        String primaryImageUrl,
        BigDecimal price,
        String briefDescription,
        String categoryName,
        String brandName
) {
}
