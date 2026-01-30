package com.prm292.techstore.services;

import com.prm292.techstore.common.mappers.ResponseMapper;
import com.prm292.techstore.constants.CartStatus;
import com.prm292.techstore.constants.OrderStatus;
import com.prm292.techstore.dtos.requests.CreateOrderRequest;
import com.prm292.techstore.dtos.responses.OrderResponse;
import com.prm292.techstore.exceptions.NotFoundException;
import com.prm292.techstore.models.Cart;
import com.prm292.techstore.models.Order;
import com.prm292.techstore.models.User;
import com.prm292.techstore.repositories.CartRepository;
import com.prm292.techstore.repositories.OrderRepository;
import com.prm292.techstore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    @Transactional
    public OrderResponse handleCreateOrder(String username, CreateOrderRequest request) {
        User user = userRepository.findFirstByUsernameIgnoreCase(username).orElseThrow(() -> new NotFoundException("User not found."));
        Cart cart = cartRepository.findFirstByUserIdAndStatus(user.getId(), CartStatus.Processing).orElseThrow(() -> new NotFoundException("Processing cart not found."));

        Order order = new Order();
        order.setCart(cart);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setBillingAddress(request.getBillingAddress());
        order.setOrderStatus(OrderStatus.Pending);
        orderRepository.save(order);

        cart.setStatus(CartStatus.Paid);
        cartRepository.save(cart);

        return ResponseMapper.mapToOrderResponse(order);
    }
}
