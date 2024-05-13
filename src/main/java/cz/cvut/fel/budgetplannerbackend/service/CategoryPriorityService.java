package cz.cvut.fel.budgetplannerbackend.service;

import cz.cvut.fel.budgetplannerbackend.dto.CategoryPriorityDto;

import java.util.List;

public interface CategoryPriorityService {

    CategoryPriorityDto setCategoryPriority(CategoryPriorityDto categoryPriorityDto);

    double calculateCategoryPriority(Long categoryId, Long dashboardId);

    List<CategoryPriorityDto> getCategoryPriorities(Long dashboardId);
}

