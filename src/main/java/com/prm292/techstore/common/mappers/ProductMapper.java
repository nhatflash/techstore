package com.prm292.techstore.common.mappers;

import com.prm292.techstore.dtos.requests.CreateProductRequest;
import com.prm292.techstore.dtos.responses.*;
import com.prm292.techstore.models.Brand;
import com.prm292.techstore.models.Category;
import com.prm292.techstore.models.Product;
import com.prm292.techstore.models.ProductImage;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class ProductMapper {

    public static Product toEntity(CreateProductRequest request, Category category, Brand brand) {
        return Product.builder()
                .productName(request.productName())
                .briefDescription(request.briefDescription())
                .fullDescription(request.fullDescription())
                .technicalSpecifications(request.technicalSpecifications())
                .price(request.price())
                .primaryImageUrl(request.primaryImageUrl())
                .category(category)
                .brand(brand)
                .images(new ArrayList<>())
                .build();
    }

    public static ProductImage toProductImage(String imageUrl, Product product) {
        return ProductImage.builder()
                .imageUrl(imageUrl)
                .product(product)
                .build();
    }

    public static ProductListResponse toListResponse(Product product) {
        return new ProductListResponse(
                product.getId(),
                product.getProductName(),
                product.getPrimaryImageUrl(),
                product.getPrice(),
                product.getBriefDescription(),
                product.getCategory() != null ? product.getCategory().getCategoryName() : null,
                product.getBrand() != null ? product.getBrand().getBrandName() : null
        );
    }

    public static ProductDetailResponse toDetailResponse(Product product) {
        List<String> additionalImageUrls = product.getImages().stream()
                .map(ProductImage::getImageUrl)
                .toList();

        CategoryResponse categoryResponse = product.getCategory() != null
                ? new CategoryResponse(product.getCategory().getId(), product.getCategory().getCategoryName())
                : null;

        BrandResponse brandResponse = product.getBrand() != null
                ? new BrandResponse(product.getBrand().getId(), product.getBrand().getBrandName())
                : null;

        return new ProductDetailResponse(
                product.getId(),
                product.getProductName(),
                product.getBriefDescription(),
                product.getFullDescription(),
                product.getTechnicalSpecifications(),
                product.getPrice(),
                product.getPrimaryImageUrl(),
                additionalImageUrls,
                categoryResponse,
                brandResponse
        );
    }

    public static PageResponse<ProductListResponse> toPageResponse(Page<Product> productPage) {
        List<ProductListResponse> content = productPage.getContent().stream()
                .map(ProductMapper::toListResponse)
                .toList();

        return new PageResponse<>(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isFirst(),
                productPage.isLast()
        );
    }
}
