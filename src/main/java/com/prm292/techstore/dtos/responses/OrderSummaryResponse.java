package com.prm292.techstore.dtos.responses;

import java.util.List;

public record OrderSummaryResponse(
    OrderResponse order,
    List<CartItemResponse> cartItems,
    PaymentResponse payment
) {
    
}
