package com.prm292.techstore.repositories;

import com.prm292.techstore.models.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {
    Optional<Brand> findByBrandNameIgnoreCase(String brandName);

    List<Brand> findByBrandNameContainingIgnoreCase(String keyword);
}
