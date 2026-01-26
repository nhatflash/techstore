package com.prm292.techstore.controllers;

import com.prm292.techstore.apis.ApiResponse;
import com.prm292.techstore.dtos.responses.CategoryResponse;
import com.prm292.techstore.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> searchCategories(@RequestParam String keyword) {
        List<CategoryResponse> categories = categoryService.searchCategoriesByKeyword(keyword);
        return ResponseEntity.ok(ApiResponse.success("Categories search results retrieved successfully", categories));
    }
}

