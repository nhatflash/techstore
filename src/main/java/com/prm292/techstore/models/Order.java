package com.prm292.techstore.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;

    @Column(name = "billing_address", nullable = false)
    private String billingAddress;

    @Column(name = "order_status", nullable = false, length = 50)
    private String orderStatus;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "order_date")
    private Instant orderDate;


}