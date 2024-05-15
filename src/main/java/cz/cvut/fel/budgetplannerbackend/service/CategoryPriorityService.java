package cz.cvut.fel.budgetplannerbackend.service;

import cz.cvut.fel.budgetplannerbackend.dto.CategoryPriorityDto;

import java.util.List;

public interface CategoryPriorityService {

    CategoryPriorityDto setCategoryPriority(CategoryPriorityDto categoryPriorityDto);

    CategoryPriorityDto updateCategoryPriority(CategoryPriorityDto categoryPriorityDto);

    void deleteCategoryPriority(Long id);

    void deleteCategoryPriorityByUserCategoryAndDashboard(Long userId, Long categoryId, Long dashboardId);

    double calculateCategoryPriority(Long categoryId, Long dashboardId);

    List<CategoryPriorityDto> getCategoryPriorities(Long dashboardId);

    List<CategoryPriorityDto> getCategoryPrioritiesByUserAndDashboard(Long userId, Long dashboardId);

    List<CategoryPriorityDto> getCategoryPrioritiesByCategoryAndDashboard(Long categoryId, Long dashboardId);

    CategoryPriorityDto getCategoryPriorityByUserAndCategoryAndDashboard(Long userId, Long categoryId, Long dashboardId);
}