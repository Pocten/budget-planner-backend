package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.dto.CategoryDto;
import cz.cvut.fel.budgetplannerbackend.entity.Category;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import cz.cvut.fel.budgetplannerbackend.entity.FinancialRecord;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.CategoryMapper;
import cz.cvut.fel.budgetplannerbackend.repository.CategoryRepository;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRepository;
import cz.cvut.fel.budgetplannerbackend.repository.FinancialRecordRepository;
import cz.cvut.fel.budgetplannerbackend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final DashboardRepository dashboardRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final CategoryMapper categoryMapper;
    private static final Logger LOG = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> findAllCategoriesByDashboardId(Long dashboardId) {
        LOG.info("Fetching all categories for dashboard id: {}", dashboardId);
        List<Category> categories = categoryRepository.findAllByDashboardId(dashboardId);
        return categories.stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto findCategoryByIdAndDashboardId(Long id, Long dashboardId) {
        LOG.info("Fetching category with id: {} for dashboard id: {}", id, dashboardId);
        Category category = categoryRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Category", id));
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public CategoryDto createCategory(Long dashboardId, CategoryDto categoryDto) {
        LOG.info("Creating new category for dashboard id: {}", dashboardId);
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard", dashboardId));
        Category category = categoryMapper.toEntity(categoryDto);
        category.setDashboard(dashboard);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long dashboardId, Long id, CategoryDto categoryDto) {
        LOG.info("Updating category with id: {} for dashboard id: {}", id, dashboardId);
        Category category = categoryRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Category", id));

        if (categoryDto.name() != null) {
            category.setName(categoryDto.name());
        }
        if (categoryDto.description() != null) {
            category.setDescription(categoryDto.description());
        }

        Category updatedCategory = categoryRepository.save(category);
        LOG.info("Updated category with id: {} for dashboard id: {}", id, dashboardId);
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long dashboardId, Long id) {
        LOG.info("Initiating deletion of category with id: {} for dashboard id: {}", id, dashboardId);
        Category category = categoryRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Category", id));

        LOG.info("Setting category_id to null for all financial records associated with category id: {}", id);
        financialRecordRepository.setCategoryToNullByCategoryId(category.getId());

        LOG.info("Category with id: {} successfully deleted, and all associated financial records are updated.", id);
        categoryRepository.delete(category);
    }
}
