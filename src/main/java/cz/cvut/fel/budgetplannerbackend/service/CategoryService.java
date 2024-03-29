package cz.cvut.fel.budgetplannerbackend.service;

import cz.cvut.fel.budgetplannerbackend.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> findAllByDashboardId(Long dashboardId);

    CategoryDto findByIdAndDashboardId(Long id, Long dashboardId);

    CategoryDto create(Long dashboardId, CategoryDto categoryDto);

    CategoryDto update(Long dashboardId, Long id, CategoryDto categoryDto);

    void delete(Long dashboardId, Long id);
}
