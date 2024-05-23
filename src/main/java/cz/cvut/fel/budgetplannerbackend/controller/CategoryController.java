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
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

/**
 * REST controller for managing categories within a dashboard.
 * This controller provides endpoints for CRUD operations on categories and for managing category priorities.
 */
@RestController
@RequestMapping("/api/v1/dashboards/{dashboardId}/categories") // Base URL for all category-related endpoints.
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryServiceImpl categoryService;
    private final CategoryPriorityServiceImpl categoryPriorityService;
    private static final Logger LOG = LoggerFactory.getLogger(CategoryController.class);

    /**
     * Retrieves all categories associated with a specific dashboard.
     *
     * @param dashboardId The ID of the dashboard.
     * @return A ResponseEntity containing a list of CategoryDto objects and an HTTP status of 200 OK.
     * @throws AccessDeniedException If the user does not have at least VIEWER access to the dashboard.
     */
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategoriesByDashboardId(@PathVariable Long dashboardId) {
        LOG.info("Received request to get all categories for dashboard with id: {}", dashboardId);
        List<CategoryDto> categoryDtos = categoryService.findAllCategoriesByDashboardId(dashboardId); // Retrieve all categories for the dashboard.
        LOG.info("Returned all categories for dashboard with id: {}", dashboardId);
        return ResponseEntity.ok(categoryDtos); // Return the categories with an OK status.
    }

    /**
     * Retrieves a specific category by its ID and dashboard ID.
     *
     * @param dashboardId The ID of the dashboard.
     * @param id          The ID of the category.
     * @return A ResponseEntity containing the CategoryDto object and an HTTP status of 200 OK if found,
     *         or 404 Not Found if not found.
     * @throws AccessDeniedException If the user does not have at least VIEWER access to the dashboard.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryByIdAndDashboardId(@PathVariable Long dashboardId, @PathVariable Long id) {
        LOG.info("Received request to get category with id: {} for dashboard with id: {}", id, dashboardId);
        try {
            CategoryDto categoryDto = categoryService.findCategoryByIdAndDashboardId(id, dashboardId); // Retrieve the category.
            LOG.info("Returned category with id: {} for dashboard with id: {}", id, dashboardId);
            return ResponseEntity.ok(categoryDto); // Return the category with an OK status.
        } catch (EntityNotFoundException e) {
            LOG.error("Error getting category", e); // Log the exception if the category is not found.
            return ResponseEntity.notFound().build(); // Return a Not Found status.
        }
    }

    /**
     * Creates a new category within a dashboard.
     *
     * @param dashboardId The ID of the dashboard to associate the category with.
     * @param categoryDto The CategoryDto object containing the data for the new category.
     * @return A ResponseEntity containing the created CategoryDto object and an HTTP status of 201 Created.
     * @throws EntityNotFoundException If the dashboard is not found.
     * @throws AccessDeniedException If the user does not have at least EDITOR access to the dashboard.
     */
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@PathVariable Long dashboardId, @RequestBody CategoryDto categoryDto) {
        LOG.info("Received request to create category for dashboard with id: {}", dashboardId);
        CategoryDto createdCategoryDto = categoryService.createCategory(dashboardId, categoryDto); // Create the category.
        LOG.info("Created category for dashboard with id: {}", dashboardId);
        return new ResponseEntity<>(createdCategoryDto, HttpStatus.CREATED); // Return the created category with a Created status.
    }

    /**
     * Updates an existing category.
     *
     * @param dashboardId The ID of the dashboard associated with the category.
     * @param id          The ID of the category to update.
     * @param categoryDto The CategoryDto object containing the updated data for the category.
     * @return A ResponseEntity containing the updated CategoryDto object and an HTTP status of 200 OK if found,
     *         or 404 Not Found if not found.
     * @throws AccessDeniedException If the user does not have at least EDITOR access to the dashboard.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long dashboardId, @PathVariable Long id, @RequestBody CategoryDto categoryDto) {
        LOG.info("Received request to update category with id: {} for dashboard with id: {}", id, dashboardId);
        try {
            CategoryDto updatedCategoryDto = categoryService.updateCategory(dashboardId, id, categoryDto); // Update the category.
            LOG.info("Updated category with id: {} for dashboard with id: {}", id, dashboardId);
            return ResponseEntity.ok(updatedCategoryDto); // Return the updated category with an OK status.
        } catch (EntityNotFoundException e) {
            LOG.error("Error updating category", e); // Log the exception if the category is not found.
            return ResponseEntity.notFound().build(); // Return a Not Found status.
        }
    }

    /**
     * Deletes a category.
     *
     * @param dashboardId The ID of the dashboard associated with the category.
     * @param id          The ID of the category to delete.
     * @return A ResponseEntity with an HTTP status of 204 No Content if successful,
     *         or 404 Not Found if the category is not found.
     * @throws AccessDeniedException If the user does not have at least EDITOR access to the dashboard.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long dashboardId, @PathVariable Long id) {
        LOG.info("Received request to delete category with id: {} for dashboard with id: {}", id, dashboardId);
        try {
            categoryService.deleteCategory(dashboardId, id); // Delete the category.
            LOG.info("Deleted category with id: {} for dashboard with id: {}", id, dashboardId);
            return ResponseEntity.noContent().build(); // Return a No Content status to indicate successful deletion.
        } catch (EntityNotFoundException e) {
            LOG.error("Error deleting category", e); // Log the exception if the category is not found.
            return ResponseEntity.notFound().build(); // Return a Not Found status.
        }
    }

    /**
     * Sets a new category priority for the currently logged-in user.
     *
     * @param dashboardId  The ID of the dashboard.
     * @param categoryId   The ID of the category.
     * @param priority     The priority value to set.
     * @param authentication The authentication object containing user details.
     * @return A ResponseEntity containing the created CategoryPriorityDto object and an HTTP status of 201 Created if successful,
     *         or 403 Forbidden if the user is not authenticated.
     */
    @PostMapping("/{categoryId}/priorities")
    public ResponseEntity<CategoryPriorityDto> setCategoryPriority(
            @PathVariable Long dashboardId,
            @PathVariable Long categoryId,
            @RequestParam Integer priority,
            Authentication authentication) {

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) { // Check if the user is authenticated.
            LOG.info("Received request to set priority for userId: {}, categoryId: {}, dashboardId: {}, priority: {}",
                    userDetails.getUserId(), categoryId, dashboardId, priority);

            // Create a new CategoryPriorityDto object.
            CategoryPriorityDto categoryPriorityDto = new CategoryPriorityDto(
                    null,
                    userDetails.getUserId(),
                    categoryId,
                    dashboardId,
                    priority
            );
            CategoryPriorityDto createdPriority = categoryPriorityService.setCategoryPriority(categoryPriorityDto); // Set the category priority.
            LOG.info("Priority set successfully for userId: {}, categoryId: {}, dashboardId: {}", userDetails.getUserId(), categoryId, dashboardId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPriority); // Return the created priority with a Created status.
        }
        LOG.warn("User authentication required to set priority");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Return a Forbidden status if not authenticated.
    }

    /**
     * Updates an existing category priority for the currently logged-in user.
     *
     * @param dashboardId  The ID of the dashboard.
     * @param categoryId   The ID of the category.
     * @param priority     The updated priority value.
     * @param authentication The authentication object containing user details.
     * @return A ResponseEntity containing the updated CategoryPriorityDto object and an HTTP status of 200 OK if successful,
     *         or 403 Forbidden if the user is not authenticated.
     */
    @PutMapping("/{categoryId}/priorities")
    public ResponseEntity<CategoryPriorityDto> updateCategoryPriority(
            @PathVariable Long dashboardId,
            @PathVariable Long categoryId,
            @RequestParam Integer priority,
            Authentication authentication) {

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) { // Check if the user is authenticated.
            LOG.info("Received request to update priority for userId: {}, categoryId: {}, dashboardId: {}, priority: {}",
                    userDetails.getUserId(), categoryId, dashboardId, priority);

            // Create a CategoryPriorityDto object with the updated priority.
            CategoryPriorityDto categoryPriorityDto = new CategoryPriorityDto(
                    null,
                    userDetails.getUserId(),
                    categoryId,
                    dashboardId,
                    priority
            );
            CategoryPriorityDto updatedPriority = categoryPriorityService.updateCategoryPriority(categoryPriorityDto); // Update the category priority.
            LOG.info("Priority updated successfully for userId: {}, categoryId: {}, dashboardId: {}", userDetails.getUserId(), categoryId, dashboardId);
            return ResponseEntity.ok(updatedPriority); // Return the updated priority with an OK status.
        }
        LOG.warn("User authentication required to update priority");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Return a Forbidden status if not authenticated.
    }

    /**
     * Deletes a category priority for the currently logged-in user.
     *
     * @param dashboardId  The ID of the dashboard.
     * @param categoryId   The ID of the category.
     * @param authentication The authentication object containing user details.
     * @return A ResponseEntity with an HTTP status of 204 No Content if successful,
     *         or 403 Forbidden if the user is not authenticated.
     */
    @DeleteMapping("/{categoryId}/priorities")
    public ResponseEntity<Void> deleteCategoryPriority(
            @PathVariable Long dashboardId,
            @PathVariable Long categoryId,
            Authentication authentication) {

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) { // Check if the user is authenticated.
            LOG.info("Received request to delete priority for userId: {}, categoryId: {}, dashboardId: {}",
                    userDetails.getUserId(), categoryId, dashboardId);
            categoryPriorityService.deleteCategoryPriorityByUserCategoryAndDashboard(userDetails.getUserId(), categoryId, dashboardId); // Delete the category priority.
            LOG.info("Priority deleted successfully for userId: {}, categoryId: {}, dashboardId: {}", userDetails.getUserId(), categoryId, dashboardId);
            return ResponseEntity.noContent().build(); // Return a No Content status to indicate successful deletion.
        }
        LOG.warn("User authentication required to delete priority");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Return a Forbidden status if not authenticated.
    }

    /**
     * Calculates the weighted average priority for a category.
     *
     * @param dashboardId The ID of the dashboard.
     * @param categoryId  The ID of the category.
     * @return A ResponseEntity containing the calculated priority as a Double and an HTTP status of 200 OK.
     * @throws AccessDeniedException If the user does not have at least VIEWER access to the dashboard.
     */
    @GetMapping("/{categoryId}/priorities/calculate")
    public ResponseEntity<Double> calculateCategoryPriority(
            @PathVariable Long dashboardId,
            @PathVariable Long categoryId) {
        LOG.info("Received request to calculate priority for categoryId: {} on dashboardId: {}", categoryId, dashboardId);
        double priority = categoryPriorityService.calculateCategoryPriority(categoryId, dashboardId); // Calculate the priority.
        LOG.info("Calculated priority for categoryId: {} on dashboardId: {} is {}", categoryId, dashboardId, priority);
        return ResponseEntity.ok(priority); // Return the calculated priority with an OK status.
    }

    /**
     * Retrieves all category priorities for a given category and dashboard.
     *
     * @param dashboardId The ID of the dashboard.
     * @param categoryId  The ID of the category.
     * @return A ResponseEntity containing a list of CategoryPriorityDto objects and an HTTP status of 200 OK.
     * @throws AccessDeniedException If the user does not have at least VIEWER access to the dashboard.
     */
    @GetMapping("/{categoryId}/priorities")
    public ResponseEntity<List<CategoryPriorityDto>> getCategoryPrioritiesByCategoryAndDashboard(
            @PathVariable Long dashboardId,
            @PathVariable Long categoryId) {
        LOG.info("Received request to get priorities for categoryId: {} on dashboardId: {}", categoryId, dashboardId);
        List<CategoryPriorityDto> priorities = categoryPriorityService.getCategoryPrioritiesByCategoryAndDashboard(categoryId, dashboardId); // Retrieve the priorities.
        LOG.info("Returned {} priorities for categoryId: {} on dashboardId: {}", priorities.size(), categoryId, dashboardId);
        return ResponseEntity.ok(priorities); // Return the priorities with an OK status.
    }

    /**
     * Retrieves all category priorities for a dashboard.
     *
     * @param dashboardId The ID of the dashboard.
     * @return A ResponseEntity containing a list of CategoryPriorityDto objects and an HTTP status of 200 OK.
     * @throws AccessDeniedException If the user does not have at least VIEWER access to the dashboard.
     */
    @GetMapping("/priorities")
    public ResponseEntity<List<CategoryPriorityDto>> getCategoryPriorities(@PathVariable Long dashboardId) {
        LOG.info("Received request to get all priorities for dashboardId: {}", dashboardId);
        List<CategoryPriorityDto> priorities = categoryPriorityService.getCategoryPriorities(dashboardId); // Retrieve the priorities.
        LOG.info("Returned {} priorities for dashboardId: {}", priorities.size(), dashboardId);
        return ResponseEntity.ok(priorities); // Return the priorities with an OK status.
    }

    /**
     * Retrieves the category priority for a specific user, category, and dashboard.
     *
     * @param dashboardId  The ID of the dashboard.
     * @param categoryId   The ID of the category.
     * @param authentication The authentication object containing user details.
     * @return A ResponseEntity containing the CategoryPriorityDto object and an HTTP status of 200 OK if found,
     *         or 403 Forbidden if the user is not authenticated.
     */
    @GetMapping("/{categoryId}/priorities/user")
    public ResponseEntity<CategoryPriorityDto> getCategoryPriorityByUserAndCategoryAndDashboard(
            @PathVariable Long dashboardId,
            @PathVariable Long categoryId,
            Authentication authentication) {

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) { // Check if the user is authenticated.
            LOG.info("Received request to get priority for userId: {}, categoryId: {}, dashboardId: {}",
                    userDetails.getUserId(), categoryId, dashboardId);
            CategoryPriorityDto priority = categoryPriorityService.getCategoryPriorityByUserAndCategoryAndDashboard(userDetails.getUserId(), categoryId, dashboardId); // Retrieve the priority.
            LOG.info("Returned priority for userId: {}, categoryId: {}, dashboardId: {}", userDetails.getUserId(), categoryId, dashboardId);
            return ResponseEntity.ok(priority); // Return the priority with an OK status.
        }
        LOG.warn("User authentication required to get priority");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Return a Forbidden status if not authenticated.
    }
}