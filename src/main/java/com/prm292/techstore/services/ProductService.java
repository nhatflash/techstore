package com.prm292.techstore.services;

import com.prm292.techstore.common.mappers.ProductMapper;
import com.prm292.techstore.dtos.requests.CreateProductRequest;
import com.prm292.techstore.dtos.requests.UpdateProductRequest;
import com.prm292.techstore.dtos.responses.PageResponse;
import com.prm292.techstore.dtos.responses.ProductDetailResponse;
import com.prm292.techstore.dtos.responses.ProductListResponse;
import com.prm292.techstore.exceptions.NotFoundException;
import com.prm292.techstore.models.Brand;
import com.prm292.techstore.models.Category;
import com.prm292.techstore.models.Product;
import com.prm292.techstore.models.ProductImage;
import com.prm292.techstore.repositories.BrandRepository;
import com.prm292.techstore.repositories.CategoryRepository;
import com.prm292.techstore.repositories.ProductRepository;
import com.prm292.techstore.specifications.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    @Transactional
    public ProductDetailResponse createProduct(CreateProductRequest request) {
        // Find or create category
        Category category = categoryRepository.findByCategoryNameIgnoreCase(request.categoryName())
                .orElseGet(() -> categoryRepository.save(
                        Category.builder()
                                .categoryName(request.categoryName())
                                .build()
                ));

        // Find or create brand
        Brand brand = brandRepository.findByBrandNameIgnoreCase(request.brandName())
                .orElseGet(() -> brandRepository.save(
                        Brand.builder()
                                .brandName(request.brandName())
                                .build()
                ));

        // Create product using mapper
        Product product = ProductMapper.toEntity(request, category, brand);
        product = productRepository.save(product);

        // Add additional images
        if (request.additionalImageUrls() != null && !request.additionalImageUrls().isEmpty()) {
            for (String imageUrl : request.additionalImageUrls()) {
                ProductImage productImage = ProductMapper.toProductImage(imageUrl, product);
                product.addImage(productImage);
            }
            product = productRepository.save(product);
        }

        return ProductMapper.toDetailResponse(product);
    }

    @Transactional
    public ProductDetailResponse updateProduct(Integer productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));

        // Update basic fields if provided
        if (request.productName() != null) {
            product.setProductName(request.productName());
        }
        if (request.briefDescription() != null) {
            product.setBriefDescription(request.briefDescription());
        }
        if (request.fullDescription() != null) {
            product.setFullDescription(request.fullDescription());
        }
        if (request.technicalSpecifications() != null) {
            product.setTechnicalSpecifications(request.technicalSpecifications());
        }
        if (request.price() != null) {
            product.setPrice(request.price());
        }
        if (request.primaryImageUrl() != null) {
            product.setPrimaryImageUrl(request.primaryImageUrl());
        }

        // Update category if provided
        if (request.categoryName() != null && !request.categoryName().isBlank()) {
            Category category = categoryRepository.findByCategoryNameIgnoreCase(request.categoryName())
                    .orElseGet(() -> categoryRepository.save(
                            Category.builder()
                                    .categoryName(request.categoryName())
                                    .build()
                    ));
            product.setCategory(category);
        }

        // Update brand if provided
        if (request.brandName() != null && !request.brandName().isBlank()) {
            Brand brand = brandRepository.findByBrandNameIgnoreCase(request.brandName())
                    .orElseGet(() -> brandRepository.save(
                            Brand.builder()
                                    .brandName(request.brandName())
                                    .build()
                    ));
            product.setBrand(brand);
        }

        // Update additional images if provided
        if (request.additionalImageUrls() != null) {
            // Clear existing images
            product.getImages().clear();

            // Add new images
            for (String imageUrl : request.additionalImageUrls()) {
                ProductImage productImage = ProductMapper.toProductImage(imageUrl, product);
                product.addImage(productImage);
            }
        }

        product = productRepository.save(product);
        return ProductMapper.toDetailResponse(product);
    }

    @Transactional
    public void deleteProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductListResponse> listProducts(
            List<Integer> brandIds,
            Integer categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String keyword,
            Pageable pageable) {

        Specification<Product> spec = Specification.where(ProductSpecification.hasBrandIds(brandIds))
                .and(ProductSpecification.hasCategoryId(categoryId))
                .and(ProductSpecification.hasPriceGreaterThanOrEqual(minPrice))
                .and(ProductSpecification.hasPriceLessThanOrEqual(maxPrice))
                .and(ProductSpecification.hasNameContaining(keyword));

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        return ProductMapper.toPageResponse(productPage);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
        return ProductMapper.toDetailResponse(product);
    }
}
