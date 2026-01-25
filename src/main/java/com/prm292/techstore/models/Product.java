package com.prm292.techstore.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Integer id;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "brief_description")
    private String briefDescription;

    @Column(name = "full_description", length = Integer.MAX_VALUE)
    private String fullDescription;

    @Column(name = "technical_specifications", length = Integer.MAX_VALUE)
    private String technicalSpecifications;

    @Column(name = "price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;


}