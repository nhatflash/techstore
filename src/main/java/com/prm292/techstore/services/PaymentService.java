package com.prm292.techstore.services;
import com.prm292.techstore.common.mappers.ResponseMapper;
import com.prm292.techstore.constants.OrderStatus;
import com.prm292.techstore.constants.PaymentMethod;
import com.prm292.techstore.dtos.responses.OrderSummaryResponse;
import com.prm292.techstore.exceptions.BadRequestException;
import com.prm292.techstore.exceptions.ForbiddenException;
import com.prm292.techstore.exceptions.NotFoundException;
import com.prm292.techstore.models.Cart;
import com.prm292.techstore.models.CartItem;
import com.prm292.techstore.models.Order;
import com.prm292.techstore.models.Payment;
import com.prm292.techstore.models.User;
import com.prm292.techstore.repositories.CartItemRepository;
import com.prm292.techstore.repositories.OrderRepository;
import com.prm292.techstore.repositories.PaymentRepository;
import com.prm292.techstore.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final VnPayService  vnPayService;
    private final PayOsService payOsService;

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
        switch (order.getPaymentMethod()) {
            case PaymentMethod.VnPay:
                Map<String, String> vnpParams = vnPayService.getVnpParams(request, order);
                return vnPayService.getQueryUrl(vnpParams);
            case PaymentMethod.PayOs:
                return payOsService.createPaymentLinkRequest(order);
        }
        throw new BadRequestException("Invalid payment method.");
    }

    @Transactional(readOnly = true)
    public OrderSummaryResponse handleGetOrderSummary(String username, int paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new NotFoundException("No payment found."));
        Order order = payment.getOrder();
        Cart cart = order.getCart();
        if (!cart.getUser().getUsername().equalsIgnoreCase(username)) {
            throw new ForbiddenException("The payment for the order that does not belong to the current user.");
        }
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        return ResponseMapper.mapToOrderSummaryResponse(payment, order, cartItems, cart.getId());
    }
}
