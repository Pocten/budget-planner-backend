package cz.cvut.fel.budgetplannerbackend.service.implementation;

import static org.junit.jupiter.api.Assertions.*;

import cz.cvut.fel.budgetplannerbackend.dto.FinancialGoalDto;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import cz.cvut.fel.budgetplannerbackend.entity.FinancialGoal;
import cz.cvut.fel.budgetplannerbackend.entity.enums.EAccessLevel;
import cz.cvut.fel.budgetplannerbackend.mapper.FinancialGoalMapper;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRepository;
import cz.cvut.fel.budgetplannerbackend.repository.FinancialGoalRepository;
import cz.cvut.fel.budgetplannerbackend.security.utils.SecurityUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


class FinancialGoalServiceImplTest {

    @Mock
    private FinancialGoalRepository financialGoalRepository;

    @Mock
    private DashboardRepository dashboardRepository;

    @Mock
    private FinancialGoalMapper financialGoalMapper;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private FinancialGoalServiceImpl financialGoalService;

    private FinancialGoal testFinancialGoal;
    private FinancialGoalDto testFinancialGoalDto;
    private Dashboard testDashboard;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        testDashboard = new Dashboard();
        testDashboard.setId(1L);

        testFinancialGoal = new FinancialGoal();
        testFinancialGoal.setId(1L);
        testFinancialGoal.setTitle("Test Goal");
        testFinancialGoal.setTargetAmount(1000.0);
        testFinancialGoal.setCurrentAmount(500.0);
        testFinancialGoal.setDeadline(LocalDate.now().plusDays(30));
        testFinancialGoal.setDashboard(testDashboard);

        testFinancialGoalDto = new FinancialGoalDto(
                1L,
                1L,
                "Test Goal",
                1000.0,
                500.0,
                LocalDate.now().plusDays(30)
        );
    }

    @Test
    void testFindAllFinancialGoalsByDashboardId() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
        when(financialGoalRepository.findByDashboardId(anyLong())).thenReturn(List.of(testFinancialGoal));
        when(financialGoalMapper.toDto(any(FinancialGoal.class))).thenReturn(testFinancialGoalDto);

        List<FinancialGoalDto> financialGoals = financialGoalService.findAllFinancialGoalsByDashboardId(1L);

        assertNotNull(financialGoals);
        assertEquals(1, financialGoals.size());
        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
        verify(financialGoalRepository, times(1)).findByDashboardId(anyLong());
    }

    @Test
    void testFindFinancialGoalByIdAndDashboardId() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
        when(financialGoalRepository.findByIdAndDashboardId(anyLong(), anyLong())).thenReturn(Optional.of(testFinancialGoal));
        when(financialGoalMapper.toDto(any(FinancialGoal.class))).thenReturn(testFinancialGoalDto);

        FinancialGoalDto financialGoal = financialGoalService.findFinancialGoalByIdAndDashboardId(1L, 1L);

        assertNotNull(financialGoal);
        assertEquals("Test Goal", financialGoal.title());
        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.VIEWER));
        verify(financialGoalRepository, times(1)).findByIdAndDashboardId(anyLong(), anyLong());
    }

    @Test
    void testCreateFinancialGoal() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        when(dashboardRepository.findById(anyLong())).thenReturn(Optional.of(testDashboard));
        when(financialGoalMapper.toEntity(any(FinancialGoalDto.class))).thenReturn(testFinancialGoal);
        when(financialGoalRepository.save(any(FinancialGoal.class))).thenReturn(testFinancialGoal);
        when(financialGoalMapper.toDto(any(FinancialGoal.class))).thenReturn(testFinancialGoalDto);

        FinancialGoalDto createdFinancialGoal = financialGoalService.createFinancialGoal(1L, testFinancialGoalDto);

        assertNotNull(createdFinancialGoal);
        assertEquals("Test Goal", createdFinancialGoal.title());
        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        verify(dashboardRepository, times(1)).findById(anyLong());
        verify(financialGoalRepository, times(1)).save(any(FinancialGoal.class));
    }

    @Test
    void testUpdateFinancialGoal() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        when(financialGoalRepository.findByIdAndDashboardId(anyLong(), anyLong())).thenReturn(Optional.of(testFinancialGoal));
        when(financialGoalRepository.save(any(FinancialGoal.class))).thenReturn(testFinancialGoal);
        when(financialGoalMapper.toDto(any(FinancialGoal.class))).thenReturn(testFinancialGoalDto);

        FinancialGoalDto updatedFinancialGoal = financialGoalService.updateFinancialGoal(1L, 1L, testFinancialGoalDto);

        assertNotNull(updatedFinancialGoal);
        assertEquals("Test Goal", updatedFinancialGoal.title());
        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        verify(financialGoalRepository, times(1)).findByIdAndDashboardId(anyLong(), anyLong());
        verify(financialGoalRepository, times(1)).save(any(FinancialGoal.class));
    }

    @Test
    void testDeleteFinancialGoal() {
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        when(financialGoalRepository.findByIdAndDashboardId(anyLong(), anyLong())).thenReturn(Optional.of(testFinancialGoal));

        financialGoalService.deleteFinancialGoal(1L, 1L);

        verify(securityUtils, times(1)).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        verify(financialGoalRepository, times(1)).findByIdAndDashboardId(anyLong(), anyLong());
        verify(financialGoalRepository, times(1)).delete(any(FinancialGoal.class));
    }
}

