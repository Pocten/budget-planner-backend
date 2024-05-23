package cz.cvut.fel.budgetplannerbackend.service.implementation;

import static org.junit.jupiter.api.Assertions.*;

import cz.cvut.fel.budgetplannerbackend.dto.BudgetDto;
import cz.cvut.fel.budgetplannerbackend.dto.DashboardDto;
import cz.cvut.fel.budgetplannerbackend.entity.Budget;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import cz.cvut.fel.budgetplannerbackend.entity.enums.EAccessLevel;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.BudgetMapper;
import cz.cvut.fel.budgetplannerbackend.repository.BudgetRepository;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRepository;
import cz.cvut.fel.budgetplannerbackend.security.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class BudgetServiceImplTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private DashboardRepository dashboardRepository;

    @Mock
    private BudgetMapper budgetMapper;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private BudgetServiceImpl budgetService;

    private Budget testBudget;
    private BudgetDto testBudgetDto;
    private Dashboard testDashboard;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testDashboard = new Dashboard();
        testDashboard.setId(1L);

        DashboardDto testDashboardDto = new DashboardDto(1L, "Test Dashboard", "Test Description", LocalDateTime.now(), 1L);

        testBudget = new Budget();
        testBudget.setId(1L);
        testBudget.setTitle("Test Budget");
        testBudget.setTotalAmount(1000.0);
        testBudget.setStartDate(LocalDate.now());
        testBudget.setEndDate(LocalDate.now().plusDays(30));
        testBudget.setDashboard(testDashboard);

        testBudgetDto = new BudgetDto(1L, testDashboardDto, "Test Budget", 1000.0, LocalDate.now(), LocalDate.now().plusDays(30));
    }

    @Test
    void testFindAllBudgetsByDashboardId() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
        when(budgetRepository.findAllByDashboardId(anyLong())).thenReturn(List.of(testBudget));
        when(budgetMapper.toDto(any(Budget.class))).thenReturn(testBudgetDto);

        List<BudgetDto> budgets = budgetService.findAllBudgetsByDashboardId(1L);

        assertNotNull(budgets);
        assertEquals(1, budgets.size());
        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
        verify(budgetRepository, times(1)).findAllByDashboardId(anyLong());
    }

    @Test
    void testFindBudgetByIdAndDashboardId() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
        when(budgetRepository.findByIdAndDashboardId(anyLong(), anyLong())).thenReturn(Optional.of(testBudget));
        when(budgetMapper.toDto(any(Budget.class))).thenReturn(testBudgetDto);

        BudgetDto budget = budgetService.findBudgetByIdAndDashboardId(1L, 1L);

        assertNotNull(budget);
        assertEquals("Test Budget", budget.title());
        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
        verify(budgetRepository, times(1)).findByIdAndDashboardId(anyLong(), anyLong());
    }

    @Test
    void testFindBudgetByIdAndDashboardId_NotFound() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
        when(budgetRepository.findByIdAndDashboardId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> budgetService.findBudgetByIdAndDashboardId(1L, 1L));
        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
        verify(budgetRepository, times(1)).findByIdAndDashboardId(anyLong(), anyLong());
    }

    @Test
    void testCreateBudget() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        when(dashboardRepository.findById(anyLong())).thenReturn(Optional.of(testDashboard));
        when(budgetMapper.toEntity(any(BudgetDto.class))).thenReturn(testBudget);
        when(budgetRepository.save(any(Budget.class))).thenReturn(testBudget);
        when(budgetMapper.toDto(any(Budget.class))).thenReturn(testBudgetDto);

        BudgetDto createdBudget = budgetService.createBudget(1L, testBudgetDto);

        assertNotNull(createdBudget);
        assertEquals("Test Budget", createdBudget.title());
        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        verify(dashboardRepository, times(1)).findById(anyLong());
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }

    @Test
    void testCreateBudget_DashboardNotFound() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        when(dashboardRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> budgetService.createBudget(1L, testBudgetDto));
        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        verify(dashboardRepository, times(1)).findById(anyLong());
    }

    @Test
    void testUpdateBudget() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        when(budgetRepository.findByIdAndDashboardId(anyLong(), anyLong())).thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class))).thenReturn(testBudget);
        when(budgetMapper.toDto(any(Budget.class))).thenReturn(testBudgetDto);

        BudgetDto updatedBudget = budgetService.updateBudget(1L, 1L, testBudgetDto);

        assertNotNull(updatedBudget);
        assertEquals("Test Budget", updatedBudget.title());
        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        verify(budgetRepository, times(1)).findByIdAndDashboardId(anyLong(), anyLong());
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }

    @Test
    void testUpdateBudget_NotFound() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        when(budgetRepository.findByIdAndDashboardId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> budgetService.updateBudget(1L, 1L, testBudgetDto));
        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        verify(budgetRepository, times(1)).findByIdAndDashboardId(anyLong(), anyLong());
    }

    @Test
    void testDeleteBudget() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        when(budgetRepository.findByIdAndDashboardId(anyLong(), anyLong())).thenReturn(Optional.of(testBudget));

        budgetService.deleteBudget(1L, 1L);

        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        verify(budgetRepository, times(1)).findByIdAndDashboardId(anyLong(), anyLong());
        verify(budgetRepository, times(1)).delete(any(Budget.class));
    }

    @Test
    void testDeleteBudget_NotFound() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        when(budgetRepository.findByIdAndDashboardId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> budgetService.deleteBudget(1L, 1L));
        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        verify(budgetRepository, times(1)).findByIdAndDashboardId(anyLong(), anyLong());
    }
}