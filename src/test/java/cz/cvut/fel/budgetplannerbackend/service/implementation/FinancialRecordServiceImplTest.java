package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.dto.CategoryDto;
import cz.cvut.fel.budgetplannerbackend.dto.DashboardDto;
import cz.cvut.fel.budgetplannerbackend.dto.FinancialRecordDto;
import cz.cvut.fel.budgetplannerbackend.entity.Category;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import cz.cvut.fel.budgetplannerbackend.entity.FinancialRecord;
import cz.cvut.fel.budgetplannerbackend.entity.User;
import cz.cvut.fel.budgetplannerbackend.entity.enums.EAccessLevel;
import cz.cvut.fel.budgetplannerbackend.entity.enums.ERecordType;
import cz.cvut.fel.budgetplannerbackend.mapper.FinancialRecordMapper;
import cz.cvut.fel.budgetplannerbackend.repository.CategoryRepository;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRepository;
import cz.cvut.fel.budgetplannerbackend.repository.FinancialRecordRepository;
import cz.cvut.fel.budgetplannerbackend.security.utils.SecurityUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FinancialRecordServiceImplTest {

    @Mock
    private FinancialRecordRepository financialRecordRepository;

    @Mock
    private DashboardRepository dashboardRepository;

    @Mock
    private FinancialRecordMapper financialRecordMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private FinancialRecordServiceImpl financialRecordService;

    private FinancialRecordDto testFinancialRecordDto;

    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setId(1L);

        Dashboard testDashboard = new Dashboard();
        testDashboard.setId(1L);

        Category testCategory = new Category();
        testCategory.setId(1L);

        FinancialRecord testFinancialRecord = new FinancialRecord();
        testFinancialRecord.setId(1L);
        testFinancialRecord.setDashboard(testDashboard);
        testFinancialRecord.setUser(testUser);
        testFinancialRecord.setCategory(testCategory);

        testFinancialRecordDto = new FinancialRecordDto(
                1L,
                testUser.getId(),
                new DashboardDto(testDashboard.getId(), "Test Dashboard", "Description", LocalDateTime.now(), testUser.getId()),
                new BigDecimal("100.0"),
                new CategoryDto(testCategory.getId(), "Test Category", "Description", null),
                ERecordType.EXPENSE,
                LocalDateTime.now(),
                "Test Description"
        );
    }

    @Test
    void testFindAllFinancialRecordsByDashboardId() {
        // Arrange
        Long dashboardId = 1L;
        List<FinancialRecord> financialRecords = List.of(new FinancialRecord(), new FinancialRecord());

        doNothing().when(securityUtils).checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        when(financialRecordRepository.findAllByDashboardId(dashboardId)).thenReturn(financialRecords);
        when(financialRecordMapper.toDto(any(FinancialRecord.class))).thenReturn(testFinancialRecordDto);

        // Act
        List<FinancialRecordDto> result = financialRecordService.findAllFinancialRecordsByDashboardId(dashboardId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(securityUtils, times(1)).checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        verify(financialRecordRepository, times(1)).findAllByDashboardId(dashboardId);
    }

    @Test
    void testFindFinancialRecordByIdAndDashboardId() {
        // Arrange
        Long id = 1L;
        Long dashboardId = 1L;
        FinancialRecord financialRecord = new FinancialRecord();

        doNothing().when(securityUtils).checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        when(financialRecordRepository.findByIdAndDashboardId(id, dashboardId)).thenReturn(Optional.of(financialRecord));
        when(financialRecordMapper.toDto(any(FinancialRecord.class))).thenReturn(testFinancialRecordDto);

        // Act
        FinancialRecordDto result = financialRecordService.findFinancialRecordByIdAndDashboardId(id, dashboardId);

        // Assert
        assertNotNull(result);
        verify(securityUtils, times(1)).checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        verify(financialRecordRepository, times(1)).findByIdAndDashboardId(id, dashboardId);
    }

    @Test
    void testCreateFinancialRecord() {
        // Arrange
        Long dashboardId = 1L;
        Long userId = 1L;
        Long categoryId = 1L;
        User testUser = new User(userId, "testUser", "test@example.com", "password", LocalDateTime.now());
        Dashboard testDashboard = new Dashboard(dashboardId, "Test Dashboard", "Description", LocalDateTime.now(), testUser);
        Category testCategory = new Category(categoryId, "Test Category", "Description", testDashboard);
        FinancialRecordDto financialRecordDto = new FinancialRecordDto(null, userId, new DashboardDto(dashboardId, "Test Dashboard", "Description", LocalDateTime.now(), userId),
                new BigDecimal("100.00"), new CategoryDto(categoryId, "Test Category", "Description", new DashboardDto(dashboardId, "Test Dashboard", "Description", LocalDateTime.now(), userId)),
                ERecordType.INCOME, LocalDateTime.now(), "Description");

        when(securityUtils.getCurrentUser()).thenReturn(testUser);
        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        when(dashboardRepository.findById(dashboardId)).thenReturn(Optional.of(testDashboard));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        when(financialRecordRepository.save(any(FinancialRecord.class))).thenAnswer(invocation -> {
            FinancialRecord savedRecord = invocation.getArgument(0);
            savedRecord.setId(1L);
            return savedRecord;
        });
        when(financialRecordMapper.toDto(any(FinancialRecord.class))).thenReturn(financialRecordDto);

        // Act
        FinancialRecordDto createdRecord = financialRecordService.createFinancialRecord(dashboardId, financialRecordDto);

        // Assert
        assertNotNull(createdRecord);
        assertEquals(financialRecordDto.amount(), createdRecord.amount());
        verify(financialRecordRepository, times(1)).save(any(FinancialRecord.class));
    }

    @Test
    void testUpdateFinancialRecord() {
        // Arrange
        Long dashboardId = 1L;
        Long recordId = 1L;
        Long userId = 1L;
        Long categoryId = 1L;
        User testUser = new User(userId, "testUser", "test@example.com", "password", LocalDateTime.now());
        Dashboard testDashboard = new Dashboard(dashboardId, "Test Dashboard", "Description", LocalDateTime.now(), testUser);
        Category testCategory = new Category(categoryId, "Test Category", "Description", testDashboard);
        FinancialRecord testRecord = new FinancialRecord(recordId, testUser, testDashboard, new BigDecimal("100.00"), testCategory, ERecordType.INCOME, LocalDateTime.now(), "Description");
        FinancialRecordDto financialRecordDto = new FinancialRecordDto(recordId, userId, new DashboardDto(dashboardId, "Test Dashboard", "Description", LocalDateTime.now(), userId),
                new BigDecimal("150.00"), new CategoryDto(categoryId, "Test Category", "Description", new DashboardDto(dashboardId, "Test Dashboard", "Description", LocalDateTime.now(), userId)),
                ERecordType.EXPENSE, LocalDateTime.now(), "Updated Description");

        doNothing().when(securityUtils).checkDashboardAccess(anyLong(), eq(EAccessLevel.EDITOR));
        when(financialRecordRepository.findByIdAndDashboardId(recordId, dashboardId)).thenReturn(Optional.of(testRecord));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        when(financialRecordRepository.save(any(FinancialRecord.class))).thenReturn(testRecord);
        when(financialRecordMapper.toDto(any(FinancialRecord.class))).thenReturn(financialRecordDto);

        // Act
        FinancialRecordDto updatedRecord = financialRecordService.updateFinancialRecord(recordId, dashboardId, financialRecordDto);

        // Assert
        assertNotNull(updatedRecord);
        assertEquals(financialRecordDto.amount(), updatedRecord.amount());
        assertEquals(financialRecordDto.description(), updatedRecord.description());
        verify(financialRecordRepository, times(1)).save(any(FinancialRecord.class));
    }

    @Test
    void testDeleteFinancialRecord() {
        // Arrange
        Long id = 1L;
        Long dashboardId = 1L;
        FinancialRecord financialRecord = new FinancialRecord();

        doNothing().when(securityUtils).checkDashboardAccess(dashboardId, EAccessLevel.EDITOR);
        when(financialRecordRepository.findByIdAndDashboardId(id, dashboardId)).thenReturn(Optional.of(financialRecord));

        // Act
        financialRecordService.deleteFinancialRecord(id, dashboardId);

        // Assert
        verify(securityUtils, times(1)).checkDashboardAccess(dashboardId, EAccessLevel.EDITOR);
        verify(financialRecordRepository, times(1)).findByIdAndDashboardId(id, dashboardId);
        verify(financialRecordRepository, times(1)).delete(financialRecord);
    }
}
