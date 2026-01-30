package com.prm292.techstore.repositories;

import com.prm292.techstore.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
