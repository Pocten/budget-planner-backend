package cz.cvut.fel.budgetplannerbackend.service;

import cz.cvut.fel.budgetplannerbackend.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> findAllCategoriesByDashboardId(Long dashboardId);

    CategoryDto findCategoryByIdAndDashboardId(Long id, Long dashboardId);

    CategoryDto createCategory(Long dashboardId, CategoryDto categoryDto);

    CategoryDto updateCategory(Long dashboardId, Long id, CategoryDto categoryDto);

    void deleteCategory(Long dashboardId, Long id);
}

