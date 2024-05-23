package cz.cvut.fel.budgetplannerbackend.service.implementation;

import static org.junit.jupiter.api.Assertions.*;

import cz.cvut.fel.budgetplannerbackend.dto.CategoryPriorityDto;
import cz.cvut.fel.budgetplannerbackend.entity.*;
import cz.cvut.fel.budgetplannerbackend.entity.enums.EAccessLevel;
import cz.cvut.fel.budgetplannerbackend.entity.enums.ERole;
import cz.cvut.fel.budgetplannerbackend.mapper.CategoryPriorityMapper;
import cz.cvut.fel.budgetplannerbackend.repository.*;
import cz.cvut.fel.budgetplannerbackend.security.utils.SecurityUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CategoryPriorityServiceImplTest {

    @Mock
    private CategoryPriorityRepository categoryPriorityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private DashboardRepository dashboardRepository;

    @Mock
    private DashboardRoleRepository dashboardRoleRepository;

    @Mock
    private FinancialRecordRepository financialRecordRepository;

    @Mock
    private CategoryPriorityMapper categoryPriorityMapper;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private CategoryPriorityServiceImpl categoryPriorityService;

    private User user1;
    private User user2;
    private User user3;
    private Category testCategory;
    private Dashboard testDashboard;
    private CategoryPriority categoryPriority1;
    private CategoryPriority categoryPriority2;
    private CategoryPriority categoryPriority3;
    private DashboardRole dashboardRole1;
    private DashboardRole dashboardRole2;
    private DashboardRole dashboardRole3;
    private CategoryPriorityDto testCategoryPriorityDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCategory = new Category();
        testCategory.setId(1L);

        testDashboard = new Dashboard();
        testDashboard.setId(1L);

        user1 = new User();
        user1.setId(1L);
        user2 = new User();
        user2.setId(2L);
        user3 = new User();
        user3.setId(3L);

        categoryPriority1 = new CategoryPriority();
        categoryPriority1.setId(1L);
        categoryPriority1.setUser(user1);
        categoryPriority1.setCategory(testCategory);
        categoryPriority1.setDashboard(testDashboard);
        categoryPriority1.setPriority(5);

        categoryPriority2 = new CategoryPriority();
        categoryPriority2.setId(2L);
        categoryPriority2.setUser(user2);
        categoryPriority2.setCategory(testCategory);
        categoryPriority2.setDashboard(testDashboard);
        categoryPriority2.setPriority(3);

        categoryPriority3 = new CategoryPriority();
        categoryPriority3.setId(3L);
        categoryPriority3.setUser(user3);
        categoryPriority3.setCategory(testCategory);
        categoryPriority3.setDashboard(testDashboard);
        categoryPriority3.setPriority(4);

        Role role1 = new Role();
        role1.setId(1L);
        role1.setName(ERole.EMPLOYEE);

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName(ERole.STUDENT);

        Role role3 = new Role();
        role3.setId(3L);
        role3.setName(ERole.RETIREE);

        dashboardRole1 = new DashboardRole();
        dashboardRole1.setId(1L);
        dashboardRole1.setRole(role1);
        dashboardRole1.setUser(user1);
        dashboardRole1.setDashboard(testDashboard);

        dashboardRole2 = new DashboardRole();
        dashboardRole2.setId(2L);
        dashboardRole2.setRole(role2);
        dashboardRole2.setUser(user2);
        dashboardRole2.setDashboard(testDashboard);

        dashboardRole3 = new DashboardRole();
        dashboardRole3.setId(3L);
        dashboardRole3.setRole(role3);
        dashboardRole3.setUser(user3);
        dashboardRole3.setDashboard(testDashboard);

        testCategoryPriorityDto = new CategoryPriorityDto(
                1L, 1L, 1L, 1L, 5
        );

    }

    @Test
    void testSetCategoryPriority() {
        CategoryPriorityDto inputDto = new CategoryPriorityDto(
                null, user1.getId(), testCategory.getId(), testDashboard.getId(), 5
        );

        when(securityUtils.getCurrentUser()).thenReturn(user1);
        doNothing().when(securityUtils).checkAuthenticatedUser(anyLong());
        when(categoryPriorityRepository.findByUserIdAndCategoryIdAndDashboardId(anyLong(), anyLong(), anyLong())).thenReturn(Optional.empty());
        when(categoryPriorityMapper.toEntity(any(CategoryPriorityDto.class))).thenReturn(categoryPriority1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));
        when(dashboardRepository.findById(anyLong())).thenReturn(Optional.of(testDashboard));
        when(categoryPriorityRepository.save(any(CategoryPriority.class))).thenReturn(categoryPriority1);
        when(categoryPriorityMapper.toDto(any(CategoryPriority.class))).thenReturn(testCategoryPriorityDto);

        CategoryPriorityDto result = categoryPriorityService.setCategoryPriority(inputDto);

        assertNotNull(result);
        assertEquals(inputDto.priority(), result.priority());
        verify(categoryPriorityRepository, times(1)).save(any(CategoryPriority.class));
    }

    @Test
    void testUpdateCategoryPriority() {
        CategoryPriorityDto updateDto = new CategoryPriorityDto(
                1L, user1.getId(), testCategory.getId(), testDashboard.getId(), 4
        );

        when(securityUtils.getCurrentUser()).thenReturn(user1);
        doNothing().when(securityUtils).checkAuthenticatedUser(anyLong());
        when(categoryPriorityRepository.findByUserIdAndCategoryIdAndDashboardId(anyLong(), anyLong(), anyLong())).thenReturn(Optional.of(categoryPriority1));
        when(categoryPriorityRepository.save(any(CategoryPriority.class))).thenReturn(categoryPriority1);
        when(categoryPriorityMapper.toDto(any(CategoryPriority.class))).thenReturn(updateDto);

        CategoryPriorityDto result = categoryPriorityService.updateCategoryPriority(updateDto);

        assertNotNull(result);
        assertEquals(updateDto.priority(), result.priority());
        verify(categoryPriorityRepository, times(1)).save(any(CategoryPriority.class));
    }

    @Test
    void testDeleteCategoryPriority() {
        when(categoryPriorityRepository.findById(anyLong())).thenReturn(Optional.of(categoryPriority1));

        categoryPriorityService.deleteCategoryPriority(1L);

        verify(categoryPriorityRepository, times(1)).delete(any(CategoryPriority.class));
    }

    @Test
    void testDeleteCategoryPriorityByUserCategoryAndDashboard() {
        when(categoryPriorityRepository.findByUserIdAndCategoryIdAndDashboardId(anyLong(), anyLong(), anyLong())).thenReturn(Optional.of(categoryPriority1));

        categoryPriorityService.deleteCategoryPriorityByUserCategoryAndDashboard(1L, 1L, 1L);

        verify(categoryPriorityRepository, times(1)).delete(any(CategoryPriority.class));
    }

    @Test
    void testGetCategoryPrioritiesByUserAndDashboard() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
        when(categoryPriorityRepository.findByUserIdAndDashboardId(anyLong(), anyLong())).thenReturn(List.of(categoryPriority1));
        when(categoryPriorityMapper.toDto(any(CategoryPriority.class))).thenReturn(testCategoryPriorityDto);

        List<CategoryPriorityDto> result = categoryPriorityService.getCategoryPrioritiesByUserAndDashboard(1L, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).userId());
        verify(categoryPriorityRepository, times(1)).findByUserIdAndDashboardId(anyLong(), anyLong());
    }

    @Test
    void testGetCategoryPrioritiesByCategoryAndDashboard() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
        when(categoryPriorityRepository.findByCategoryIdAndDashboardId(anyLong(), anyLong())).thenReturn(List.of(categoryPriority1));
        when(categoryPriorityMapper.toDto(any(CategoryPriority.class))).thenReturn(testCategoryPriorityDto);

        List<CategoryPriorityDto> result = categoryPriorityService.getCategoryPrioritiesByCategoryAndDashboard(1L, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).categoryId());
        verify(categoryPriorityRepository, times(1)).findByCategoryIdAndDashboardId(anyLong(), anyLong());
    }

    @Test
    void testGetCategoryPriorityByUserAndCategoryAndDashboard() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
        when(categoryPriorityRepository.findByUserIdAndCategoryIdAndDashboardId(anyLong(), anyLong(), anyLong())).thenReturn(Optional.of(categoryPriority1));
        when(categoryPriorityMapper.toDto(any(CategoryPriority.class))).thenReturn(testCategoryPriorityDto);

        CategoryPriorityDto result = categoryPriorityService.getCategoryPriorityByUserAndCategoryAndDashboard(1L, 1L, 1L);

        assertNotNull(result);
        assertEquals(testCategoryPriorityDto.priority(), result.priority());
        assertEquals(1L, result.userId());
        assertEquals(1L, result.categoryId());
        assertEquals(1L, result.dashboardId());
        verify(categoryPriorityRepository, times(1)).findByUserIdAndCategoryIdAndDashboardId(anyLong(), anyLong(), anyLong());
    }

//    @Test
//    void testCalculateCategoryPriority() {
//        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
//        when(categoryPriorityRepository.findByCategoryIdAndDashboardId(anyLong(), anyLong()))
//                .thenReturn(List.of(categoryPriority1, categoryPriority2, categoryPriority3));
//
//        // Setup predefined responses for repository methods
//        when(dashboardRoleRepository.findByUserIdAndDashboardId(eq(1L), anyLong())).thenReturn(Optional.of(dashboardRole1));
//        when(dashboardRoleRepository.findByUserIdAndDashboardId(eq(2L), anyLong())).thenReturn(Optional.of(dashboardRole2));
//        when(dashboardRoleRepository.findByUserIdAndDashboardId(eq(3L), anyLong())).thenReturn(Optional.of(dashboardRole3));
//
//        // Predefine income values
//        when(financialRecordRepository.sumIncomeByDashboardId(eq(1L))).thenReturn(BigDecimal.TEN);
//        when(financialRecordRepository.sumIncomeByUserIdAndDashboardId(eq(1L), eq(1L))).thenReturn(BigDecimal.ONE);
//        when(financialRecordRepository.sumIncomeByUserIdAndDashboardId(eq(2L), eq(1L))).thenReturn(BigDecimal.valueOf(2));
//        when(financialRecordRepository.sumIncomeByUserIdAndDashboardId(eq(3L), eq(1L))).thenReturn(BigDecimal.valueOf(3));
//
//        // Perform the priority calculation
//        double result = categoryPriorityService.calculateCategoryPriority(1L, 1L);
//
//        // Verify the result
//        assertNotNull(result);
//
//        // Verify method invocations
//        verify(categoryPriorityRepository, times(1)).findByCategoryIdAndDashboardId(anyLong(), anyLong());
//        verify(dashboardRoleRepository, times(1)).findByUserIdAndDashboardId(eq(1L), eq(1L));
//        verify(dashboardRoleRepository, times(1)).findByUserIdAndDashboardId(eq(2L), eq(1L));
//        verify(dashboardRoleRepository, times(1)).findByUserIdAndDashboardId(eq(3L), eq(1L));
//        verify(financialRecordRepository, times(1)).sumIncomeByDashboardId(eq(1L));
//        verify(financialRecordRepository, times(1)).sumIncomeByUserIdAndDashboardId(eq(1L), eq(1L));
//        verify(financialRecordRepository, times(1)).sumIncomeByUserIdAndDashboardId(eq(2L), eq(1L));
//        verify(financialRecordRepository, times(1)).sumIncomeByUserIdAndDashboardId(eq(3L), eq(1L));
//    }

    @Test
    void testGetCategoryPriorities() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
        when(categoryPriorityRepository.findByDashboardId(anyLong())).thenReturn(List.of(categoryPriority1));
        when(categoryPriorityMapper.toDto(any(CategoryPriority.class))).thenReturn(testCategoryPriorityDto);

        List<CategoryPriorityDto> result = categoryPriorityService.getCategoryPriorities(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoryPriorityRepository, times(1)).findByDashboardId(anyLong());

    }
}
