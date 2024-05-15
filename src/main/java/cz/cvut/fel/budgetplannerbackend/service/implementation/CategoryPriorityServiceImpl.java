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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

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

    @Override
    @Transactional
    public CategoryPriorityDto setCategoryPriority(CategoryPriorityDto categoryPriorityDto) {
        User currentUser = securityUtils.getCurrentUser();
        LOG.info("Setting priority for userId: {}, categoryId: {}, dashboardId: {}, priority: {}",
                currentUser.getId(), categoryPriorityDto.categoryId(), categoryPriorityDto.dashboardId(), categoryPriorityDto.priority());

        securityUtils.checkAuthenticatedUser(currentUser.getId());

        Optional<CategoryPriority> existingCategoryPriority = categoryPriorityRepository.findByUserIdAndCategoryIdAndDashboardId(
                currentUser.getId(), categoryPriorityDto.categoryId(), categoryPriorityDto.dashboardId());

        if (existingCategoryPriority.isPresent()) {
            throw new IllegalArgumentException("Priority for this category, user, and dashboard already exists.");
        }

        CategoryPriority categoryPriority = categoryPriorityMapper.toEntity(categoryPriorityDto);

        categoryPriority.setUser(userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + currentUser.getId())));
        categoryPriority.setCategory(categoryRepository.findById(categoryPriorityDto.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryPriorityDto.categoryId())));
        categoryPriority.setDashboard(dashboardRepository.findById(categoryPriorityDto.dashboardId())
                .orElseThrow(() -> new EntityNotFoundException("Dashboard not found with id: " + categoryPriorityDto.dashboardId())));

        LOG.debug("Saving new category priority");
        categoryPriority = categoryPriorityRepository.save(categoryPriority);

        LOG.info("Priority set successfully for userId: {}, categoryId: {}, dashboardId: {}", categoryPriorityDto.userId(), categoryPriorityDto.categoryId(), categoryPriorityDto.dashboardId());
        return categoryPriorityMapper.toDto(categoryPriority);
    }

    @Override
    @Transactional
    public CategoryPriorityDto updateCategoryPriority(CategoryPriorityDto categoryPriorityDto) {
        User currentUser = securityUtils.getCurrentUser();
        LOG.info("Updating priority for userId: {}, categoryId: {}, dashboardId: {}, priority: {}",
                currentUser.getId(), categoryPriorityDto.categoryId(), categoryPriorityDto.dashboardId(), categoryPriorityDto.priority());

        securityUtils.checkAuthenticatedUser(currentUser.getId());

        CategoryPriority categoryPriority = categoryPriorityRepository.findByUserIdAndCategoryIdAndDashboardId(
                        currentUser.getId(), categoryPriorityDto.categoryId(), categoryPriorityDto.dashboardId())
                .orElseThrow(() -> new EntityNotFoundException("CategoryPriority not found for userId: "
                        + currentUser.getId() + ", categoryId: "
                        + categoryPriorityDto.categoryId() + ", dashboardId: "
                        + categoryPriorityDto.dashboardId()));

        categoryPriority.setPriority(categoryPriorityDto.priority());
        categoryPriority = categoryPriorityRepository.save(categoryPriority);

        LOG.info("Priority updated successfully for userId: {}, categoryId: {}, dashboardId: {}", categoryPriorityDto.userId(), categoryPriorityDto.categoryId(), categoryPriorityDto.dashboardId());
        return categoryPriorityMapper.toDto(categoryPriority);
    }

    @Override
    @Transactional
    public void deleteCategoryPriority(Long id) {
        LOG.info("Deleting priority for categoryPriorityId: {}", id);
        CategoryPriority categoryPriority = categoryPriorityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CategoryPriority not found with id: " + id));
        categoryPriorityRepository.delete(categoryPriority);
        LOG.info("Priority deleted successfully for categoryPriorityId: {}", id);
    }

    @Override
    @Transactional
    public void deleteCategoryPriorityByUserCategoryAndDashboard(Long userId, Long categoryId, Long dashboardId) {
        LOG.info("Deleting priority for userId: {}, categoryId: {}, dashboardId: {}", userId, categoryId, dashboardId);
        CategoryPriority categoryPriority = categoryPriorityRepository.findByUserIdAndCategoryIdAndDashboardId(userId, categoryId, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("CategoryPriority not found for userId: " + userId + ", categoryId: " + categoryId + ", dashboardId: " + dashboardId));
        categoryPriorityRepository.delete(categoryPriority);
        LOG.info("Priority deleted successfully for userId: {}, categoryId: {}, dashboardId: {}", userId, categoryId, dashboardId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryPriorityDto> getCategoryPrioritiesByUserAndDashboard(Long userId, Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Fetching priorities for userId: {} and dashboardId: {}", userId, dashboardId);
        List<CategoryPriority> priorities = categoryPriorityRepository.findByUserIdAndDashboardId(userId, dashboardId);
        List<CategoryPriorityDto> priorityDtos = priorities.stream()
                .map(categoryPriorityMapper::toDto)
                .toList();
        LOG.info("Fetched {} priorities for userId: {} and dashboardId: {}", priorityDtos.size(), userId, dashboardId);
        return priorityDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryPriorityDto> getCategoryPrioritiesByCategoryAndDashboard(Long categoryId, Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Fetching priorities for categoryId: {} and dashboardId: {}", categoryId, dashboardId);
        List<CategoryPriority> priorities = categoryPriorityRepository.findByCategoryIdAndDashboardId(categoryId, dashboardId);
        List<CategoryPriorityDto> priorityDtos = priorities.stream()
                .map(categoryPriorityMapper::toDto)
                .toList();
        LOG.info("Fetched {} priorities for categoryId: {} and dashboardId: {}", priorityDtos.size(), categoryId, dashboardId);
        return priorityDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryPriorityDto getCategoryPriorityByUserAndCategoryAndDashboard(Long userId, Long categoryId, Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Fetching priority for userId: {}, categoryId: {}, dashboardId: {}", userId, categoryId, dashboardId);
        CategoryPriority categoryPriority = categoryPriorityRepository.findByUserIdAndCategoryIdAndDashboardId(userId, categoryId, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("CategoryPriority not found for userId: " + userId + ", categoryId: " + categoryId + ", dashboardId: " + dashboardId));
        LOG.info("Fetched priority for userId: {}, categoryId: {}, dashboardId: {}", userId, categoryId, dashboardId);
        return categoryPriorityMapper.toDto(categoryPriority);
    }

    @Override
    @Transactional
    public double calculateCategoryPriority(Long categoryId, Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Calculating priority for categoryId: {} on dashboardId: {}", categoryId, dashboardId);

        List<CategoryPriority> priorities = categoryPriorityRepository.findByCategoryIdAndDashboardId(categoryId, dashboardId);
        BigDecimal totalPriority = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (CategoryPriority cp : priorities) {
            double roleWeight = getRoleWeight(cp.getUser().getId(), dashboardId);
            double incomeWeight = getIncomeWeight(cp.getUser().getId(), dashboardId);
            double combinedWeight = (roleWeight * 0.5) + (incomeWeight * 0.5);

            BigDecimal priority = BigDecimal.valueOf(cp.getPriority());
            BigDecimal combinedWeightBD = BigDecimal.valueOf(combinedWeight);

            totalPriority = totalPriority.add(priority.multiply(combinedWeightBD));
            totalWeight = totalWeight.add(combinedWeightBD);

            LOG.debug("UserId: {}, RoleWeight: {}, IncomeWeight: {}, CombinedWeight: {}, Priority: {}, TotalPriority: {}, TotalWeight: {}",
                    cp.getUser().getId(), roleWeight, incomeWeight, combinedWeight, cp.getPriority(), totalPriority, totalWeight);
        }

        BigDecimal calculatedPriorityBD = totalWeight.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : totalPriority.divide(totalWeight, 2, RoundingMode.HALF_UP);
        double calculatedPriority = calculatedPriorityBD.doubleValue();
        LOG.info("Calculated priority for categoryId: {} on dashboardId: {} is {}", categoryId, dashboardId, calculatedPriority);

        return calculatedPriority;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryPriorityDto> getCategoryPriorities(Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Fetching all priorities for dashboardId: {}", dashboardId);
        List<CategoryPriorityDto> priorities = categoryPriorityRepository.findByDashboardId(dashboardId).stream()
                .map(categoryPriorityMapper::toDto)
                .toList();
        LOG.info("Fetched {} priorities for dashboardId: {}", priorities.size(), dashboardId);
        return priorities;
    }

    private double getRoleWeight(Long userId, Long dashboardId) {
        LOG.debug("Fetching role for userId: {} on dashboardId: {}", userId, dashboardId);
        DashboardRole role = dashboardRoleRepository.findByUserIdAndDashboardId(userId, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found for userId: " + userId + " on dashboardId: " + dashboardId));
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
        return weight;
    }

    private double getIncomeWeight(Long userId, Long dashboardId) {
        LOG.debug("Calculating income weight for userId: {} on dashboardId: {}", userId, dashboardId);
        BigDecimal totalIncome = financialRecordRepository.sumIncomeByDashboardId(dashboardId);
        BigDecimal userIncome = financialRecordRepository.sumIncomeByUserIdAndDashboardId(userId, dashboardId);

        if (userIncome == null) {
            LOG.warn("No income records found for userId: {} on dashboardId: {}", userId, dashboardId);
            userIncome = BigDecimal.ZERO;
        }

        if (totalIncome == null) {
            LOG.warn("No income records found for dashboardId: {}", dashboardId);
            totalIncome = BigDecimal.ZERO;
        }

        double weight = totalIncome.compareTo(BigDecimal.ZERO) == 0 ? 0 : userIncome.divide(totalIncome, RoundingMode.HALF_UP).doubleValue();
        LOG.debug("Income weight for userId: {} is {}", userId, weight);
        return weight;
    }
}
