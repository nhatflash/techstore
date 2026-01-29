package com.prm292.techstore.dtos.requests;

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
public class ManageProductToCartRequest {
    @NotNull(message = "Product Id is required.")
    private int productId;

    @NotNull(message = "Quantity is required.")
    @Min(value = 1, message = "Product quantity must be greater than 0.")
    private int quantity;
}
