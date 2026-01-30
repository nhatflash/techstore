package com.prm292.techstore.repositories;

import com.prm292.techstore.constants.OrderStatus;
import com.prm292.techstore.models.Cart;
import com.prm292.techstore.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findFirstById(Integer id);

    boolean existsByCartAndOrderStatusNot(Cart cart, String orderStatus);
}
