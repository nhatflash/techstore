package com.prm292.techstore.common.mappers;

import com.prm292.techstore.dtos.responses.*;
import com.prm292.techstore.models.Cart;
import com.prm292.techstore.models.CartItem;
import com.prm292.techstore.models.Order;
import com.prm292.techstore.models.User;

import java.util.ArrayList;
import java.util.List;


public class ResponseMapper {


    public static SignInResponse mapToSignInResponse(String accessToken, String refreshToken) {
        return SignInResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .build();
    }

    public static CartResponse mapToCartResponse(Cart cart, List<CartItem> items) {
        return new CartResponse(
                cart.getId(),
                cart.getUser().getId(),
                mapToCartItemResponseList(items),
                cart.getTotalPrice()
        );
    }

    public static CartItemResponse mapToCartItemResponse(CartItem item) {
        return new CartItemResponse(
                item.getId(),
                item.getCart().getId(),
                item.getProduct().getProductName(),
                item.getProduct().getPrimaryImageUrl(),
                item.getQuantity(),
                item.getPrice()
        );
    }

    public static List<CartItemResponse> mapToCartItemResponseList(List<CartItem> items) {
        List<CartItemResponse> responses = new ArrayList<>();
        for (CartItem item : items) {
            responses.add(mapToCartItemResponse(item));
        }
        return responses;
    }

    public static OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCart().getId(),
                order.getPaymentMethod(),
                order.getBillingAddress(),
                order.getOrderStatus(),
                order.getOrderDate()
        );
    }
}
