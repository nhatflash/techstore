package com.prm292.techstore.repositories;

import com.prm292.techstore.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findFirstByUsernameIgnoreCase(String username);

    Optional<User> findFirstByEmailIgnoreCase(String email);

    Optional<User> findFirstByPhoneNumber(String phoneNumber);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
