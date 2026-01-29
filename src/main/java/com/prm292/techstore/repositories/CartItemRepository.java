package com.prm292.techstore.repositories;

import com.prm292.techstore.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    Optional<CartItem> findFirstByCartIdAndProductId(Integer cartId, Integer productId);

    boolean existsByCartIdAndProductId(Integer cartId, Integer productId);

    List<CartItem> findByCartId(Integer cartId);

    Optional<CartItem> findFirstById(Integer id);
}
