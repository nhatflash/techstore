package com.prm292.techstore.services;

import com.prm292.techstore.dtos.responses.CategoryResponse;
import com.prm292.techstore.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> new CategoryResponse(category.getId(), category.getCategoryName()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> searchCategoriesByKeyword(String keyword) {
        return categoryRepository.findByCategoryNameContainingIgnoreCase(keyword)
                .stream()
                .map(category -> new CategoryResponse(category.getId(), category.getCategoryName()))
                .toList();
    }
}

