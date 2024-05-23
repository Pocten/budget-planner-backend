package cz.cvut.fel.budgetplannerbackend.service.implementation;

import static org.junit.jupiter.api.Assertions.*;

import cz.cvut.fel.budgetplannerbackend.dto.CategoryDto;
import cz.cvut.fel.budgetplannerbackend.dto.DashboardDto;
import cz.cvut.fel.budgetplannerbackend.entity.Category;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import cz.cvut.fel.budgetplannerbackend.entity.enums.EAccessLevel;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.CategoryMapper;
import cz.cvut.fel.budgetplannerbackend.repository.CategoryPriorityRepository;
import cz.cvut.fel.budgetplannerbackend.repository.CategoryRepository;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRepository;
import cz.cvut.fel.budgetplannerbackend.repository.FinancialRecordRepository;
import cz.cvut.fel.budgetplannerbackend.security.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryPriorityRepository categoryPriorityRepository;

    @Mock
    private DashboardRepository dashboardRepository;

    @Mock
    private FinancialRecordRepository financialRecordRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Dashboard testDashboard;
    private Category testCategory;
    private CategoryDto testCategoryDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testDashboard = new Dashboard();
        testDashboard.setId(1L);

        DashboardDto testDashboardDto = new DashboardDto(1L, "Test Dashboard", "Test Description", LocalDateTime.now(), 1L);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");
        testCategory.setDescription("Test Description");
        testCategory.setDashboard(testDashboard);

        testCategoryDto = new CategoryDto(1L, "Test Category", "Test Description", testDashboardDto);
    }

    @Test
    void testFindAllCategoriesByDashboardId() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
        when(categoryRepository.findAllByDashboardId(anyLong())).thenReturn(List.of(testCategory));
        when(categoryMapper.toDto(any(Category.class))).thenReturn(testCategoryDto);

        List<CategoryDto> categories = categoryService.findAllCategoriesByDashboardId(1L);

        assertNotNull(categories);
        assertEquals(1, categories.size());
        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
        verify(categoryRepository, times(1)).findAllByDashboardId(anyLong());
    }

    @Test
    void testFindCategoryByIdAndDashboardId() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
        when(categoryRepository.findByIdAndDashboardId(anyLong(), anyLong())).thenReturn(Optional.of(testCategory));
        when(categoryMapper.toDto(any(Category.class))).thenReturn(testCategoryDto);

        CategoryDto category = categoryService.findCategoryByIdAndDashboardId(1L, 1L);

        assertNotNull(category);
        assertEquals("Test Category", category.name());
        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
        verify(categoryRepository, times(1)).findByIdAndDashboardId(anyLong(), anyLong());
    }

    @Test
    void testFindCategoryByIdAndDashboardId_NotFound() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
        when(categoryRepository.findByIdAndDashboardId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.findCategoryByIdAndDashboardId(1L, 1L));
        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
        verify(categoryRepository, times(1)).findByIdAndDashboardId(anyLong(), anyLong());
    }

    @Test
    void testCreateCategory() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        when(dashboardRepository.findById(anyLong())).thenReturn(Optional.of(testDashboard));
        when(categoryMapper.toEntity(any(CategoryDto.class))).thenReturn(testCategory);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(testCategoryDto);

        CategoryDto createdCategory = categoryService.createCategory(1L, testCategoryDto);

        assertNotNull(createdCategory);
        assertEquals("Test Category", createdCategory.name());
        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        verify(dashboardRepository, times(1)).findById(anyLong());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testCreateCategory_DashboardNotFound() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        when(dashboardRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.createCategory(1L, testCategoryDto));
        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        verify(dashboardRepository, times(1)).findById(anyLong());
    }

    @Test
    void testUpdateCategory() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        when(categoryRepository.findByIdAndDashboardId(anyLong(), anyLong())).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(testCategoryDto);

        CategoryDto updatedCategory = categoryService.updateCategory(1L, 1L, testCategoryDto);

        assertNotNull(updatedCategory);
        assertEquals("Test Category", updatedCategory.name());
        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        verify(categoryRepository, times(1)).findByIdAndDashboardId(anyLong(), anyLong());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testUpdateCategory_NotFound() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        when(categoryRepository.findByIdAndDashboardId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.updateCategory(1L, 1L, testCategoryDto));
        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        verify(categoryRepository, times(1)).findByIdAndDashboardId(anyLong(), anyLong());
    }

    @Test
    void testDeleteCategory() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        when(categoryRepository.findByIdAndDashboardId(anyLong(), anyLong())).thenReturn(Optional.of(testCategory));

        categoryService.deleteCategory(1L, 1L);

        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        verify(categoryRepository, times(1)).findByIdAndDashboardId(anyLong(), anyLong());
        verify(categoryPriorityRepository, times(1)).deleteByCategoryId(anyLong());
        verify(financialRecordRepository, times(1)).setCategoryToNullByCategoryId(anyLong());
        verify(categoryRepository, times(1)).delete(any(Category.class));
    }

    @Test
    void testDeleteCategory_NotFound() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        when(categoryRepository.findByIdAndDashboardId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.deleteCategory(1L, 1L));
        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        verify(categoryRepository, times(1)).findByIdAndDashboardId(anyLong(), anyLong());
    }
}