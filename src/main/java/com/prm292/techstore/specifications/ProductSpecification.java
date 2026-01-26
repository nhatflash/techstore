package com.prm292.techstore.specifications;

import com.prm292.techstore.models.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> hasBrandIds(List<Integer> brandIds) {
        return (root, query, criteriaBuilder) -> {
            if (brandIds == null || brandIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("brand").get("id").in(brandIds);
        };
    }

    public static Specification<Product> hasCategoryId(Integer categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("category").get("id"), categoryId);
        };
    }

    public static Specification<Product> hasPriceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
        };
    }

    public static Specification<Product> hasPriceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (maxPrice == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    public static Specification<Product> hasNameContaining(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("productName")),
                    "%" + keyword.toLowerCase() + "%"
            );
        };
    }
}
