package com.prm292.techstore.dtos.requests;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdjustProductQuantityInCartRequest {
    @NotNull(message = "Product ID is required")
    private int productId;

    @Min(value = -100, message = "Cart adjustment quantity must not be lower than -100")
    @Max(value = 100, message = "Cart adjustment quantity must not be greater than 100")
    @NotNull(message = "Product quantity is required")
    private int quantity;
}
