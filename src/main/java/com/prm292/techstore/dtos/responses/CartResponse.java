package com.prm292.techstore.dtos.responses;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Integer id,
        Integer userId,
        List<CartItemResponse> items,
        BigDecimal totalPrice
) {
}
