package com.prm292.techstore.repositories;

import com.prm292.techstore.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);

    List<Category> findByCategoryNameContainingIgnoreCase(String keyword);
}
