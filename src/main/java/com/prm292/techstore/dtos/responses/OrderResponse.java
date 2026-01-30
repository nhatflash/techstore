package com.prm292.techstore.dtos.responses;

import java.time.Instant;

public record OrderResponse(
        Integer id,
        Integer cartId,
        String paymentMethod,
        String billingAddress,
        String orderStatus,
        Instant orderDate) {
}
