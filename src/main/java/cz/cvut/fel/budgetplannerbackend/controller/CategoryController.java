package cz.cvut.fel.budgetplannerbackend.controller;

import cz.cvut.fel.budgetplannerbackend.dto.CategoryDto;
import cz.cvut.fel.budgetplannerbackend.dto.CategoryPriorityDto;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.security.model.CustomUserDetails;
import cz.cvut.fel.budgetplannerbackend.service.implementation.CategoryPriorityServiceImpl;
import cz.cvut.fel.budgetplannerbackend.service.implementation.CategoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboards/{dashboardId}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryServiceImpl categoryService;
    private final CategoryPriorityServiceImpl categoryPriorityService;
    private static final Logger LOG = LoggerFactory.getLogger(CategoryController.class);

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategoriesByDashboardId(@PathVariable Long dashboardId) {
        LOG.info("Received request to get all categories for dashboard with id: {}", dashboardId);
        List<CategoryDto> categoryDtos = categoryService.findAllCategoriesByDashboardId(dashboardId);
        LOG.info("Returned all categories for dashboard with id: {}", dashboardId);
        return ResponseEntity.ok(categoryDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryByIdAndDashboardId(@PathVariable Long dashboardId, @PathVariable Long id) {
        LOG.info("Received request to get category with id: {} for dashboard with id: {}", id, dashboardId);
        try {
            CategoryDto categoryDto = categoryService.findCategoryByIdAndDashboardId(id, dashboardId);
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
        CategoryDto createdCategoryDto = categoryService.createCategory(dashboardId, categoryDto);
        LOG.info("Created category for dashboard with id: {}", dashboardId);
        return new ResponseEntity<>(createdCategoryDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long dashboardId, @PathVariable Long id, @RequestBody CategoryDto categoryDto) {
        LOG.info("Received request to update category with id: {} for dashboard with id: {}", id, dashboardId);
        try {
            CategoryDto updatedCategoryDto = categoryService.updateCategory(dashboardId, id, categoryDto);
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
            categoryService.deleteCategory(dashboardId, id);
            LOG.info("Deleted category with id: {} for dashboard with id: {}", id, dashboardId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            LOG.error("Error deleting category", e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{categoryId}/priorities")
    public ResponseEntity<CategoryPriorityDto> setCategoryPriority(
            @PathVariable Long dashboardId,
            @PathVariable Long categoryId,
            @RequestParam Integer priority,
            Authentication authentication) {

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            LOG.info("Received request to set priority for userId: {}, categoryId: {}, dashboardId: {}, priority: {}",
                    userDetails.getUserId(), categoryId, dashboardId, priority);
            CategoryPriorityDto categoryPriorityDto = new CategoryPriorityDto(
                    null,
                    userDetails.getUserId(),
                    categoryId,
                    dashboardId,
                    priority
            );
            CategoryPriorityDto createdPriority = categoryPriorityService.setCategoryPriority(categoryPriorityDto);
            LOG.info("Priority set successfully for userId: {}, categoryId: {}, dashboardId: {}", userDetails.getUserId(), categoryId, dashboardId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPriority);
        }
        LOG.warn("User authentication required to set priority");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    @PutMapping("/{categoryId}/priorities")
    public ResponseEntity<CategoryPriorityDto> updateCategoryPriority(
            @PathVariable Long dashboardId,
            @PathVariable Long categoryId,
            @RequestParam Integer priority,
            Authentication authentication) {

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            LOG.info("Received request to update priority for userId: {}, categoryId: {}, dashboardId: {}, priority: {}",
                    userDetails.getUserId(), categoryId, dashboardId, priority);
            CategoryPriorityDto categoryPriorityDto = new CategoryPriorityDto(
                    null,
                    userDetails.getUserId(),
                    categoryId,
                    dashboardId,
                    priority
            );
            CategoryPriorityDto updatedPriority = categoryPriorityService.updateCategoryPriority(categoryPriorityDto);
            LOG.info("Priority updated successfully for userId: {}, categoryId: {}, dashboardId: {}", userDetails.getUserId(), categoryId, dashboardId);
            return ResponseEntity.ok(updatedPriority);
        }
        LOG.warn("User authentication required to update priority");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    @DeleteMapping("/{categoryId}/priorities")
    public ResponseEntity<Void> deleteCategoryPriority(
            @PathVariable Long dashboardId,
            @PathVariable Long categoryId,
            Authentication authentication) {

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            LOG.info("Received request to delete priority for userId: {}, categoryId: {}, dashboardId: {}",
                    userDetails.getUserId(), categoryId, dashboardId);
            categoryPriorityService.deleteCategoryPriorityByUserCategoryAndDashboard(userDetails.getUserId(), categoryId, dashboardId);
            LOG.info("Priority deleted successfully for userId: {}, categoryId: {}, dashboardId: {}", userDetails.getUserId(), categoryId, dashboardId);
            return ResponseEntity.noContent().build();
        }
        LOG.warn("User authentication required to delete priority");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/{categoryId}/priorities/calculate")
    public ResponseEntity<Double> calculateCategoryPriority(
            @PathVariable Long dashboardId,
            @PathVariable Long categoryId) {
        LOG.info("Received request to calculate priority for categoryId: {} on dashboardId: {}", categoryId, dashboardId);
        double priority = categoryPriorityService.calculateCategoryPriority(categoryId, dashboardId);
        LOG.info("Calculated priority for categoryId: {} on dashboardId: {} is {}", categoryId, dashboardId, priority);
        return ResponseEntity.ok(priority);
    }

    @GetMapping("/{categoryId}/priorities")
    public ResponseEntity<List<CategoryPriorityDto>> getCategoryPrioritiesByCategoryAndDashboard(
            @PathVariable Long dashboardId,
            @PathVariable Long categoryId) {
        LOG.info("Received request to get priorities for categoryId: {} on dashboardId: {}", categoryId, dashboardId);
        List<CategoryPriorityDto> priorities = categoryPriorityService.getCategoryPrioritiesByCategoryAndDashboard(categoryId, dashboardId);
        LOG.info("Returned {} priorities for categoryId: {} on dashboardId: {}", priorities.size(), categoryId, dashboardId);
        return ResponseEntity.ok(priorities);
    }

    @GetMapping("/priorities")
    public ResponseEntity<List<CategoryPriorityDto>> getCategoryPriorities(@PathVariable Long dashboardId) {
        LOG.info("Received request to get all priorities for dashboardId: {}", dashboardId);
        List<CategoryPriorityDto> priorities = categoryPriorityService.getCategoryPriorities(dashboardId);
        LOG.info("Returned {} priorities for dashboardId: {}", priorities.size(), dashboardId);
        return ResponseEntity.ok(priorities);
    }

    @GetMapping("/{categoryId}/priorities/user")
    public ResponseEntity<CategoryPriorityDto> getCategoryPriorityByUserAndCategoryAndDashboard(
            @PathVariable Long dashboardId,
            @PathVariable Long categoryId,
            Authentication authentication) {

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            LOG.info("Received request to get priority for userId: {}, categoryId: {}, dashboardId: {}",
                    userDetails.getUserId(), categoryId, dashboardId);
            CategoryPriorityDto priority = categoryPriorityService.getCategoryPriorityByUserAndCategoryAndDashboard(userDetails.getUserId(), categoryId, dashboardId);
            LOG.info("Returned priority for userId: {}, categoryId: {}, dashboardId: {}", userDetails.getUserId(), categoryId, dashboardId);
            return ResponseEntity.ok(priority);
        }
        LOG.warn("User authentication required to get priority");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }
}
