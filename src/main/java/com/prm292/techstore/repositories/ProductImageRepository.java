package com.prm292.techstore.repositories;

import com.prm292.techstore.models.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    List<ProductImage> findByProductId(Integer productId);

    void deleteByProductId(Integer productId);
}
