package com.prm292.techstore.services;

import com.prm292.techstore.dtos.responses.BrandResponse;
import com.prm292.techstore.repositories.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    @Transactional(readOnly = true)
    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll()
                .stream()
                .map(brand -> new BrandResponse(brand.getId(), brand.getBrandName()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BrandResponse> searchBrandsByKeyword(String keyword) {
        return brandRepository.findByBrandNameContainingIgnoreCase(keyword)
                .stream()
                .map(brand -> new BrandResponse(brand.getId(), brand.getBrandName()))
                .toList();
    }
}
