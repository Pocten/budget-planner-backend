package cz.cvut.fel.budgetplannerbackend.controller;

import cz.cvut.fel.budgetplannerbackend.dto.CategoryDto;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.service.CategoryService;
import cz.cvut.fel.budgetplannerbackend.service.implementation.CategoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboards/{dashboardId}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryServiceImpl categoryService;
    private static final Logger LOG = LoggerFactory.getLogger(CategoryController.class);

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategoriesByDashboardId(@PathVariable Long dashboardId) {
        LOG.info("Received request to get all categories for dashboard with id: {}", dashboardId);
        List<CategoryDto> categoryDtos = categoryService.findAllByDashboardId(dashboardId);
        LOG.info("Returned all categories for dashboard with id: {}", dashboardId);
        return ResponseEntity.ok(categoryDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryByIdAndDashboardId(@PathVariable Long dashboardId, @PathVariable Long id) {
        LOG.info("Received request to get category with id: {} for dashboard with id: {}", id, dashboardId);
        try {
            CategoryDto categoryDto = categoryService.findByIdAndDashboardId(id, dashboardId);
            LOG.info("Returned category with id: {} for dashboard with id: {}", id, dashboardId);
            return ResponseEntity.ok(categoryDto);
        } catch (EntityNotFoundException e) {
            LOG.error("Error getting category", e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@PathVariable Long dashboardId, @RequestBody CategoryDto categoryDto) {
        LOG.info("Received request to create category for dashboard with id: {}", dashboardId);
        CategoryDto createdCategoryDto = categoryService.create(dashboardId, categoryDto);
        LOG.info("Created category for dashboard with id: {}", dashboardId);
        return new ResponseEntity<>(createdCategoryDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long dashboardId, @PathVariable Long id, @RequestBody CategoryDto categoryDto) {
        LOG.info("Received request to update category with id: {} for dashboard with id: {}", id, dashboardId);
        try {
            CategoryDto updatedCategoryDto = categoryService.update(dashboardId, id, categoryDto);
            LOG.info("Updated category with id: {} for dashboard with id: {}", id, dashboardId);
            return ResponseEntity.ok(updatedCategoryDto);
        } catch (EntityNotFoundException e) {
            LOG.error("Error updating category", e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long dashboardId, @PathVariable Long id) {
        LOG.info("Received request to delete category with id: {} for dashboard with id: {}", id, dashboardId);
        try {
            categoryService.delete(dashboardId, id);
            LOG.info("Deleted category with id: {} for dashboard with id: {}", id, dashboardId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            LOG.error("Error deleting category", e);
            return ResponseEntity.notFound().build();
        }
    }
}
