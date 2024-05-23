package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.dto.CategoryPriorityDto;
import cz.cvut.fel.budgetplannerbackend.entity.CategoryPriority;
import cz.cvut.fel.budgetplannerbackend.entity.DashboardRole;
import cz.cvut.fel.budgetplannerbackend.entity.User;
import cz.cvut.fel.budgetplannerbackend.entity.enums.EAccessLevel;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.CategoryPriorityMapper;
import cz.cvut.fel.budgetplannerbackend.repository.*;
import cz.cvut.fel.budgetplannerbackend.security.utils.SecurityUtils;
import cz.cvut.fel.budgetplannerbackend.service.CategoryPriorityService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing category priorities for users within a dashboard.
 */
@Service
@RequiredArgsConstructor
public class CategoryPriorityServiceImpl implements CategoryPriorityService {

    private final CategoryPriorityRepository categoryPriorityRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final DashboardRepository dashboardRepository;
    private final DashboardRoleRepository dashboardRoleRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final CategoryPriorityMapper categoryPriorityMapper;
    private final SecurityUtils securityUtils;

    private static final Logger LOG = LoggerFactory.getLogger(CategoryPriorityServiceImpl.class);

    /**
     * Sets a new category priority for the currently logged-in user.
     * If a priority for the same category, user, and dashboard already exists, it throws an exception.
     *
     * @param categoryPriorityDto DTO containing information about the category priority to set.
     * @return The newly created CategoryPriorityDto object.
     * @throws IllegalArgumentException If a priority for the specified category, user, and dashboard already exists.
     * @throws EntityNotFoundException If any of the related entities (User, Category, Dashboard) are not found.
     */
    @Override
    @Transactional
    public CategoryPriorityDto setCategoryPriority(CategoryPriorityDto categoryPriorityDto) {
        User currentUser = securityUtils.getCurrentUser(); // Retrieve the currently authenticated user.
        LOG.info("Setting priority for userId: {}, categoryId: {}, dashboardId: {}, priority: {}",
                currentUser.getId(), categoryPriorityDto.categoryId(), categoryPriorityDto.dashboardId(), categoryPriorityDto.priority());

        securityUtils.checkAuthenticatedUser(currentUser.getId()); // Verify that the current user is authorized.

        // Check if a priority already exists for this user, category, and dashboard.
        Optional<CategoryPriority> existingCategoryPriority = categoryPriorityRepository.findByUserIdAndCategoryIdAndDashboardId(
                currentUser.getId(), categoryPriorityDto.categoryId(), categoryPriorityDto.dashboardId());

        if (existingCategoryPriority.isPresent()) {
            // Throw an exception if a priority already exists.
            throw new IllegalArgumentException("Priority for this category, user, and dashboard already exists.");
        }

        CategoryPriority categoryPriority = categoryPriorityMapper.toEntity(categoryPriorityDto);

        // Set the User, Category, and Dashboard entities for the CategoryPriority object.
        categoryPriority.setUser(userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + currentUser.getId())));
        categoryPriority.setCategory(categoryRepository.findById(categoryPriorityDto.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryPriorityDto.categoryId())));
        categoryPriority.setDashboard(dashboardRepository.findById(categoryPriorityDto.dashboardId())
                .orElseThrow(() -> new EntityNotFoundException("Dashboard not found with id: " + categoryPriorityDto.dashboardId())));

        LOG.debug("Saving new category priority");
        categoryPriority = categoryPriorityRepository.save(categoryPriority); // Save the new category priority.

        LOG.info("Priority set successfully for userId: {}, categoryId: {}, dashboardId: {}", categoryPriorityDto.userId(), categoryPriorityDto.categoryId(), categoryPriorityDto.dashboardId());
        return categoryPriorityMapper.toDto(categoryPriority); // Return the DTO representation of the saved priority.
    }

    /**
     * Updates an existing category priority for the currently logged-in user.
     *
     * @param categoryPriorityDto DTO containing updated information about the category priority.
     * @return The updated CategoryPriorityDto object.
     * @throws EntityNotFoundException If the category priority to update is not found.
     */
    @Override
    @Transactional
    public CategoryPriorityDto updateCategoryPriority(CategoryPriorityDto categoryPriorityDto) {
        User currentUser = securityUtils.getCurrentUser();
        LOG.info("Updating priority for userId: {}, categoryId: {}, dashboardId: {}, priority: {}",
                currentUser.getId(), categoryPriorityDto.categoryId(), categoryPriorityDto.dashboardId(), categoryPriorityDto.priority());

        securityUtils.checkAuthenticatedUser(currentUser.getId());

        // Retrieve the existing category priority from the database.
        CategoryPriority categoryPriority = categoryPriorityRepository.findByUserIdAndCategoryIdAndDashboardId(
                        currentUser.getId(), categoryPriorityDto.categoryId(), categoryPriorityDto.dashboardId())
                .orElseThrow(() -> new EntityNotFoundException("CategoryPriority not found for userId: "
                        + currentUser.getId() + ", categoryId: "
                        + categoryPriorityDto.categoryId() + ", dashboardId: "
                        + categoryPriorityDto.dashboardId()));

        // Update the priority value.
        categoryPriority.setPriority(categoryPriorityDto.priority());
        categoryPriority = categoryPriorityRepository.save(categoryPriority); // Save the updated category priority.

        LOG.info("Priority updated successfully for userId: {}, categoryId: {}, dashboardId: {}", categoryPriorityDto.userId(), categoryPriorityDto.categoryId(), categoryPriorityDto.dashboardId());
        return categoryPriorityMapper.toDto(categoryPriority); // Return the DTO representation of the updated priority.
    }

    /**
     * Deletes a category priority by its ID.
     *
     * @param id The ID of the category priority to delete.
     * @throws EntityNotFoundException If the category priority with the specified ID is not found.
     */
    @Override
    @Transactional
    public void deleteCategoryPriority(Long id) {
        LOG.info("Deleting priority for categoryPriorityId: {}", id);
        // Retrieve the category priority from the database.
        CategoryPriority categoryPriority = categoryPriorityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CategoryPriority not found with id: " + id));
        categoryPriorityRepository.delete(categoryPriority); // Delete the category priority.
        LOG.info("Priority deleted successfully for categoryPriorityId: {}", id);
    }

    /**
     * Deletes a category priority based on user ID, category ID, and dashboard ID.
     *
     * @param userId      The ID of the user.
     * @param categoryId  The ID of the category.
     * @param dashboardId The ID of the dashboard.
     * @throws EntityNotFoundException If the category priority is not found for the given IDs.
     */
    @Override
    @Transactional
    public void deleteCategoryPriorityByUserCategoryAndDashboard(Long userId, Long categoryId, Long dashboardId) {
        LOG.info("Deleting priority for userId: {}, categoryId: {}, dashboardId: {}", userId, categoryId, dashboardId);
        // Retrieve the category priority from the database.
        CategoryPriority categoryPriority = categoryPriorityRepository.findByUserIdAndCategoryIdAndDashboardId(userId, categoryId, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("CategoryPriority not found for userId: " + userId + ", categoryId: " + categoryId + ", dashboardId: " + dashboardId));
        categoryPriorityRepository.delete(categoryPriority); // Delete the category priority.
        LOG.info("Priority deleted successfully for userId: {}, categoryId: {}, dashboardId: {}", userId, categoryId, dashboardId);
    }

    /**
     * Retrieves a list of category priority DTOs for a given user and dashboard.
     *
     * @param userId      The ID of the user.
     * @param dashboardId The ID of the dashboard.
     * @return A list of CategoryPriorityDto objects.
     * @throws AccessDeniedException If the user does not have viewer access to the dashboard.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryPriorityDto> getCategoryPrioritiesByUserAndDashboard(Long userId, Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Fetching priorities for userId: {} and dashboardId: {}", userId, dashboardId);
        // Retrieve the category priorities from the database.
        List<CategoryPriority> priorities = categoryPriorityRepository.findByUserIdAndDashboardId(userId, dashboardId);
        List<CategoryPriorityDto> priorityDtos = priorities.stream()
                .map(categoryPriorityMapper::toDto) // Map the entities to DTOs.
                .toList();
        LOG.info("Fetched {} priorities for userId: {} and dashboardId: {}", priorityDtos.size(), userId, dashboardId);
        return priorityDtos; // Return the list of category priority DTOs.
    }

    /**
     * Retrieves a list of category priority DTOs for a given category and dashboard.
     *
     * @param categoryId  The ID of the category.
     * @param dashboardId The ID of the dashboard.
     * @return A list of CategoryPriorityDto objects.
     * @throws AccessDeniedException If the user does not have viewer access to the dashboard.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryPriorityDto> getCategoryPrioritiesByCategoryAndDashboard(Long categoryId, Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Fetching priorities for categoryId: {} and dashboardId: {}", categoryId, dashboardId);
        List<CategoryPriority> priorities = categoryPriorityRepository.findByCategoryIdAndDashboardId(categoryId, dashboardId);
        List<CategoryPriorityDto> priorityDtos = priorities.stream()
                .map(categoryPriorityMapper::toDto) // Map the entities to DTOs.
                .toList();
        LOG.info("Fetched {} priorities for categoryId: {} and dashboardId: {}", priorityDtos.size(), categoryId, dashboardId);
        return priorityDtos;
    }

    /**
     * Retrieves a category priority DTO for a specific user, category, and dashboard.
     *
     * @param userId      The ID of the user.
     * @param categoryId  The ID of the category.
     * @param dashboardId The ID of the dashboard.
     * @return The CategoryPriorityDto object.
     * @throws EntityNotFoundException If the category priority is not found for the given IDs.
     * @throws AccessDeniedException If the user does not have viewer access to the dashboard.
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryPriorityDto getCategoryPriorityByUserAndCategoryAndDashboard(Long userId, Long categoryId, Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Fetching priority for userId: {}, categoryId: {}, dashboardId: {}", userId, categoryId, dashboardId);
        CategoryPriority categoryPriority = categoryPriorityRepository.findByUserIdAndCategoryIdAndDashboardId(userId, categoryId, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("CategoryPriority not found for userId: " + userId + ", categoryId: " + categoryId + ", dashboardId: " + dashboardId));
        LOG.info("Fetched priority for userId: {}, categoryId: {}, dashboardId: {}", userId, categoryId, dashboardId);
        return categoryPriorityMapper.toDto(categoryPriority); // Return the DTO representation of the category priority.
    }

    /**
     * Calculates the weighted average priority for a category based on user roles and income contributions.
     *
     * @param categoryId  The ID of the category.
     * @param dashboardId The ID of the dashboard.
     * @return The calculated weighted average priority as a double value.
     * @throws AccessDeniedException If the user does not have viewer access to the dashboard.
     */
    @Override
    @Transactional
    public double calculateCategoryPriority(Long categoryId, Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Calculating priority for categoryId: {} on dashboardId: {}", categoryId, dashboardId);

        // Retrieve all priorities for the specified category and dashboard
        List<CategoryPriority> priorities = categoryPriorityRepository.findByCategoryIdAndDashboardId(categoryId, dashboardId);
        BigDecimal totalPriority = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        // Loop through each priority to calculate the weighted average
        for (CategoryPriority cp : priorities) {
            double roleWeight = getRoleWeight(cp.getUser().getId(), dashboardId); // Get the role weight for the user.
            double incomeWeight = getIncomeWeight(cp.getUser().getId(), dashboardId); // Get the income weight for the user.
            double combinedWeight = (roleWeight * 0.5) + (incomeWeight * 0.5); // Combine the weights (equal importance).

            BigDecimal priority = BigDecimal.valueOf(cp.getPriority()); // Convert priority to BigDecimal.
            BigDecimal combinedWeightBD = BigDecimal.valueOf(combinedWeight); // Convert combined weight to BigDecimal.

            totalPriority = totalPriority.add(priority.multiply(combinedWeightBD)); // Multiply priority by weight and add to total.
            totalWeight = totalWeight.add(combinedWeightBD); // Add weight to total weight.

            LOG.debug("UserId: {}, RoleWeight: {}, IncomeWeight: {}, CombinedWeight: {}, Priority: {}, TotalPriority: {}, TotalWeight: {}",
                    cp.getUser().getId(), roleWeight, incomeWeight, combinedWeight, cp.getPriority(), totalPriority, totalWeight);
        }

        // Calculate the weighted average priority, handling cases where totalWeight is zero.
        BigDecimal calculatedPriorityBD = totalWeight.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : totalPriority.divide(totalWeight, 2, RoundingMode.HALF_UP);
        double calculatedPriority = calculatedPriorityBD.doubleValue(); // Convert to double.

        LOG.info("Calculated priority for categoryId: {} on dashboardId: {} is {}", categoryId, dashboardId, calculatedPriority);

        return calculatedPriority;
    }

    /**
     * Retrieves all category priorities for a given dashboard.
     *
     * @param dashboardId The ID of the dashboard.
     * @return A list of CategoryPriorityDto objects representing the priorities.
     * @throws AccessDeniedException If the user does not have viewer access to the dashboard.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryPriorityDto> getCategoryPriorities(Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Fetching all priorities for dashboardId: {}", dashboardId);
        // Retrieve all category priorities for the dashboard and map them to DTOs.
        List<CategoryPriorityDto> priorities = categoryPriorityRepository.findByDashboardId(dashboardId).stream()
                .map(categoryPriorityMapper::toDto)
                .toList();
        LOG.info("Fetched {} priorities for dashboardId: {}", priorities.size(), dashboardId);
        return priorities; // Return the list of CategoryPriorityDto objects.
    }

    /**
     * Determines the weight of a user's role within a dashboard for priority calculations.
     *
     * @param userId      The ID of the user.
     * @param dashboardId The ID of the dashboard.
     * @return The role weight as a double value.
     * @throws EntityNotFoundException If the role is not found for the given user and dashboard.
     */
    private double getRoleWeight(Long userId, Long dashboardId) {
        LOG.debug("Fetching role for userId: {} on dashboardId: {}", userId, dashboardId);
        // Retrieve the DashboardRole for the user on the specified dashboard.
        DashboardRole role = dashboardRoleRepository.findByUserIdAndDashboardId(userId, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found for userId: " + userId + " on dashboardId: " + dashboardId));

        // Assign a weight based on the user's role in the dashboard.
        double weight = switch (role.getRole().getName()) {
            case ENTREPRENEUR -> 0.8;
            case EMPLOYEE -> 0.7;
            case RETIREE -> 0.5;
            case HOUSEMAKER -> 0.4;
            case STUDENT -> 0.3;
            case CHILD -> 0.2;
            case NONE -> 0.1;
        };

        LOG.debug("Role weight for userId: {} is {}", userId, weight);
        return weight; // Return the calculated role weight.
    }

    /**
     * Calculates the weight of a user's income contribution within a dashboard for priority calculations.
     *
     * @param userId      The ID of the user.
     * @param dashboardId The ID of the dashboard.
     * @return The income weight as a double value.
     */
    private double getIncomeWeight(Long userId, Long dashboardId) {
        LOG.debug("Calculating income weight for userId: {} on dashboardId: {}", userId, dashboardId);
        // Calculate total income for the dashboard and income for the specific user.
        BigDecimal totalIncome = financialRecordRepository.sumIncomeByDashboardId(dashboardId);
        BigDecimal userIncome = financialRecordRepository.sumIncomeByUserIdAndDashboardId(userId, dashboardId);

        // Handle cases where no income records are found for the user or dashboard.
        if (userIncome == null) {
            LOG.warn("No income records found for userId: {} on dashboardId: {}", userId, dashboardId);
            userIncome = BigDecimal.ZERO;
        }

        if (totalIncome == null) {
            LOG.warn("No income records found for dashboardId: {}", dashboardId);
            totalIncome = BigDecimal.ZERO;
        }

        // Calculate the income weight as the proportion of the user's income to the total income.
        double weight = totalIncome.compareTo(BigDecimal.ZERO) == 0 ? 0 : userIncome.divide(totalIncome, RoundingMode.HALF_UP).doubleValue();
        LOG.debug("Income weight for userId: {} is {}", userId, weight);
        return weight;
    }
}