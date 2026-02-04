package com.prm292.techstore.services;


import com.prm292.techstore.constants.CartStatus;
import com.prm292.techstore.constants.OrderStatus;
import com.prm292.techstore.constants.PaymentMethod;
import com.prm292.techstore.exceptions.BadRequestException;
import com.prm292.techstore.exceptions.ForbiddenException;
import com.prm292.techstore.exceptions.NotFoundException;
import com.prm292.techstore.models.Cart;
import com.prm292.techstore.models.Order;
import com.prm292.techstore.models.User;
import com.prm292.techstore.repositories.OrderRepository;
import com.prm292.techstore.repositories.PaymentRepository;
import com.prm292.techstore.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final VnPayService  vnPayService;

    @Transactional(readOnly = true)
    public String handleGetPaymentUrl(String username, Integer orderId, HttpServletRequest request) {
        User user = userRepository.findFirstByUsernameIgnoreCase(username).orElseThrow(() -> new NotFoundException("User not found."));
        Order order = orderRepository.findFirstById(orderId).orElseThrow(() -> new NotFoundException("Order not found."));
        Cart cart = order.getCart();
        if (!cart.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("This order does not belong to this user.");
        }
        if (!order.getOrderStatus().equals(OrderStatus.Pending)) {
            throw new BadRequestException("This order is already paid.");
        }
        if (orderRepository.existsByCartAndOrderStatusNot(cart, OrderStatus.Pending)) {
            throw new BadRequestException("The cart for this order is containing a paid order.");
        }
        if (order.getPaymentMethod().equals(PaymentMethod.VnPay)) {
            Map<String, String> vnpParams = vnPayService.getVnpParams(request, order);
            return vnpParams.get("paymentUrl");
        }
        throw new BadRequestException("Invalid payment method.");
    }
}
