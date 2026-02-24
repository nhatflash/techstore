package com.prm292.techstore.common.mappers;

import com.prm292.techstore.dtos.responses.*;
import com.prm292.techstore.models.Cart;
import com.prm292.techstore.models.CartItem;
import com.prm292.techstore.models.Order;
import com.prm292.techstore.models.Payment;
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
                mapToCartItemResponseList(items, cart.getId()),
                cart.getTotalPrice()
        );
    }

    public static CartItemResponse mapToCartItemResponse(CartItem item, int cartId) {
        return new CartItemResponse(
                item.getId(),
                cartId,
                item.getProduct().getProductName(),
                item.getProduct().getPrimaryImageUrl(),
                item.getQuantity(),
                item.getPrice()
        );
    }

    public static List<CartItemResponse> mapToCartItemResponseList(List<CartItem> items, int cartId) {
        List<CartItemResponse> responses = new ArrayList<>();
        for (CartItem item : items) {
            responses.add(mapToCartItemResponse(item, cartId));
        }
        return responses;
    }

    public static OrderResponse mapToOrderResponse(Order order, int cartId) {
        return new OrderResponse(
                order.getId(),
                cartId,
                order.getPaymentMethod(),
                order.getBillingAddress(),
                order.getOrderStatus(),
                order.getOrderDate()
        );
    }

    public static PaymentResponse mapToPaymentResponse(Payment payment, int orderId) {
        return new PaymentResponse(
            payment.getId(),
            orderId,
            payment.getAmount(),
            payment.getPaymentDate(),
            payment.getPaymentStatus()
        );
    }

    public static OrderSummaryResponse mapToOrderSummaryResponse(Payment payment, Order order, List<CartItem> cartItems, int cartId) {
        return new OrderSummaryResponse(
            mapToOrderResponse(order, cartId),
            mapToCartItemResponseList(cartItems, cartId),
            mapToPaymentResponse(payment, order.getId())
        );
    }
}
