package com.prm292.techstore.repositories;

import com.prm292.techstore.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    List<Cart> findByUserIdAndStatus(Integer userId, String status);

    Optional<Cart> findFirstByUserIdAndStatus(Integer userId, String status);


}
