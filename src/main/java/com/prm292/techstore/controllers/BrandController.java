package com.prm292.techstore.controllers;

import com.prm292.techstore.apis.ApiResponse;
import com.prm292.techstore.dtos.responses.BrandResponse;
import com.prm292.techstore.services.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAllBrands() {
        List<BrandResponse> brands = brandService.getAllBrands();
        return ResponseEntity.ok(ApiResponse.success("Brands retrieved successfully", brands));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BrandResponse>>> searchBrands(@RequestParam String keyword) {
        List<BrandResponse> brands = brandService.searchBrandsByKeyword(keyword);
        return ResponseEntity.ok(ApiResponse.success("Brands search results retrieved successfully", brands));
    }
}
