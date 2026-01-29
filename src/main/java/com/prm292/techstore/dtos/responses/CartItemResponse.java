package com.prm292.techstore.dtos.responses;

import java.math.BigDecimal;

public record CartItemResponse(
        Integer id,
        Integer cartId,
        String productName,
        String productImage,
        int quantity,
        BigDecimal price
) {
}
