package com.prm292.techstore.dtos.responses;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Integer id,
        Integer orderId,
        BigDecimal amount,
        Instant paymentDate,
        String paymentStatus
) {
}
