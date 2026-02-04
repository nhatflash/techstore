package com.prm292.techstore.services;

import com.prm292.techstore.constants.OrderStatus;
import com.prm292.techstore.exceptions.NotFoundException;
import com.prm292.techstore.models.Order;
import com.prm292.techstore.models.Payment;
import com.prm292.techstore.repositories.OrderRepository;
import com.prm292.techstore.repositories.PaymentRepository;
import com.prm292.techstore.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.model.v1.payouts.PayoutRequests;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.webhooks.Webhook;

@Service
@RequiredArgsConstructor
public class PayOsService {

    @Value("${payos.clientId}")
    private String clientId;

    @Value("${payos.apiKey}")
    private String apiKey;

    @Value("${payos.checksum}")
    private String checksumKey;

    @Value("${payos.cancelUrl}")
    private String cancelUrl;

    @Value("${payos.returnUrl}")
    private String returnUrl;

    private final RedisUtils redisUtils;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    private static PayOS payOs;

    public String createPaymentLinkRequest(Order order) {
        if (payOs == null) {
            payOs = new PayOS(clientId, apiKey, checksumKey);
        }
        var orderCode = System.currentTimeMillis() / 1000;
        CreatePaymentLinkRequest request = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount(2000L)
                .description("Thanh toán đơn hàng")
                .cancelUrl(cancelUrl)
                .returnUrl(returnUrl)
                .build();
        var paymentLink = payOs.paymentRequests().create(request);
        var orderKey = redisUtils.getOrderPrefixKey(orderCode);
        redisUtils.saveIntToString(orderKey, order.getId());
        return paymentLink.getCheckoutUrl();
    }

    @Transactional
    public void handleWebhook(Webhook webhook) {
        if (payOs == null) {
            payOs = new PayOS(clientId, apiKey, checksumKey);
        }
        var verifiedData = payOs.webhooks().verify(webhook);
        if (verifiedData.getCode().equals("00")) {
            var orderKey = redisUtils.getOrderPrefixKey(verifiedData.getOrderCode());
            var orderId = redisUtils.getIntFromString(orderKey);
            var order = orderRepository.findFirstById(orderId).orElseThrow(() -> new NotFoundException("Order Not Found"));
            if (!order.getOrderStatus().equals(OrderStatus.Pending)) {
                // trường hợp order đã được thanh toán nhưng vẫn tiếp tục nhận tiền
                redisUtils.removeItem(orderKey);
                PayoutRequests payoutRequests = PayoutRequests.builder()
                        .toAccountNumber(verifiedData.getCounterAccountNumber())
                        .toBin(verifiedData.getCounterAccountBankId())
                        .amount(verifiedData.getAmount())
                        .description("Refund for order: " + orderId)
                        .build();
                payOs.payouts().create(payoutRequests);
            } else {
                order.setOrderStatus(OrderStatus.Processing);
                orderRepository.save(order);
                var payment = new Payment();
                payment.setAmount(order.getCart().getTotalPrice());
                payment.setPaymentStatus("Success");
                payment.setOrder(order);
                paymentRepository.save(payment);
                redisUtils.removeItem(orderKey);
            }
        }
    }
}
