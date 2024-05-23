package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.dto.FinancialRecordDto;
import cz.cvut.fel.budgetplannerbackend.entity.*;
import cz.cvut.fel.budgetplannerbackend.entity.enums.EAccessLevel;
import cz.cvut.fel.budgetplannerbackend.entity.enums.ERecordType;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.FinancialRecordMapper;
import cz.cvut.fel.budgetplannerbackend.repository.CategoryRepository;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRepository;
import cz.cvut.fel.budgetplannerbackend.repository.FinancialRecordRepository;
import cz.cvut.fel.budgetplannerbackend.security.utils.SecurityUtils;
import cz.cvut.fel.budgetplannerbackend.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing financial records.
 */
@Service
@RequiredArgsConstructor
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private final FinancialRecordRepository financialRecordRepository;
    private final DashboardRepository dashboardRepository;
    private final FinancialRecordMapper financialRecordMapper;
    private final CategoryRepository categoryRepository;
    private final SecurityUtils securityUtils;

    private static final Logger LOG = LoggerFactory.getLogger(FinancialRecordServiceImpl.class);

    /**
     * Retrieves all financial records associated with a specific dashboard.
     *
     * @param dashboardId The ID of the dashboard.
     * @return A list of financial record DTOs.
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialRecordDto> findAllFinancialRecordsByDashboardId(Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Fetching all financial records for dashboard id: {}", dashboardId);
        List<FinancialRecord> financialRecords = financialRecordRepository.findAllByDashboardId(dashboardId);
        return financialRecords.stream()
                .map(financialRecordMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a specific financial record by its ID and dashboard ID.
     *
     * @param id          The ID of the financial record.
     * @param dashboardId The ID of the dashboard.
     * @return The financial record DTO.
     * @throws EntityNotFoundException If the financial record is not found.
     */
    @Override
    @Transactional(readOnly = true)
    public FinancialRecordDto findFinancialRecordByIdAndDashboardId(Long id, Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Fetching financial record with id: {} for dashboard id: {}", id, dashboardId);
        FinancialRecord financialRecord = financialRecordRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("FinancialRecord", id));
        return financialRecordMapper.toDto(financialRecord);
    }

    /**
     * Creates a new financial record.
     *
     * @param dashboardId          The ID of the dashboard to associate the financial record with.
     * @param financialRecordDto The financial record DTO containing the data for the new record.
     * @return The created financial record DTO.
     * @throws EntityNotFoundException If the dashboard or category (if provided) is not found.
     */
    @Override
    @Transactional
    public FinancialRecordDto createFinancialRecord(Long dashboardId, FinancialRecordDto financialRecordDto) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.EDITOR);
        LOG.info("Creating financial record for dashboard id: {}", dashboardId);
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard not found with id: " + dashboardId));

        Category category = null;
        if (financialRecordDto.category() != null && financialRecordDto.category().id() != null) {
            category = categoryRepository.findById(financialRecordDto.category().id())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + financialRecordDto.category().id()));
        }

        User currentUser = securityUtils.getCurrentUser();
        LOG.info("Current user id: {}", currentUser.getId());

        FinancialRecord financialRecord = new FinancialRecord();
        financialRecord.setDashboard(dashboard);
        financialRecord.setUser(currentUser); // Set the current user
        financialRecord.setAmount(financialRecordDto.amount());
        financialRecord.setCategory(category);
        financialRecord.setType(financialRecordDto.type() != null ? financialRecordDto.type() : ERecordType.INCOME);
        financialRecord.setDate(financialRecordDto.date() != null ? financialRecordDto.date() : LocalDateTime.now());
        financialRecord.setDescription(financialRecordDto.description());

        FinancialRecord savedRecord = financialRecordRepository.save(financialRecord);
        LOG.info("Created new financial record with id: {} for dashboard id: {}", savedRecord.getId(), dashboardId);
        return financialRecordMapper.toDto(savedRecord);
    }

    /**
     * Updates an existing financial record.
     *
     * @param id                   The ID of the financial record to update.
     * @param dashboardId          The ID of the dashboard associated with the financial record.
     * @param financialRecordDto The financial record DTO containing the updated data.
     * @return The updated financial record DTO.
     * @throws EntityNotFoundException If the financial record or category (if provided) is not found.
     */
    @Override
    @Transactional
    public FinancialRecordDto updateFinancialRecord(Long id, Long dashboardId, FinancialRecordDto financialRecordDto) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.EDITOR);
        LOG.info("Updating financial record with id: {} for dashboard id: {}", id, dashboardId);
        FinancialRecord financialRecord = financialRecordRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("FinancialRecord not found with id: " + id + " for dashboard id: " + dashboardId));

        financialRecord.setAmount(financialRecordDto.amount());
        financialRecord.setDescription(financialRecordDto.description());
        financialRecord.setDate(financialRecordDto.date() != null ? financialRecordDto.date() : financialRecord.getDate());
        financialRecord.setType(financialRecordDto.type() != null ? financialRecordDto.type() : financialRecord.getType());

        if (financialRecordDto.category() != null && financialRecordDto.category().id() != null) {
            Category category = categoryRepository.findById(financialRecordDto.category().id())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + financialRecordDto.category().id()));
            financialRecord.setCategory(category);
        } else {
            financialRecord.setCategory(null);
        }

        FinancialRecord updatedRecord = financialRecordRepository.save(financialRecord);
        LOG.info("Updated financial record with id: {} for dashboard id: {}", id, dashboardId);
        return financialRecordMapper.toDto(updatedRecord);
    }

    /**
     * Deletes a financial record.
     *
     * @param id          The ID of the financial record to delete.
     * @param dashboardId The ID of the dashboard associated with the financial record.
     * @throws EntityNotFoundException If the financial record is not found.
     */
    @Override
    @Transactional
    public void deleteFinancialRecord(Long id, Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.EDITOR);
        LOG.info("Deleting financial record with id: {} for dashboard id: {}", id, dashboardId);
        FinancialRecord financialRecord = financialRecordRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("FinancialRecord not found with id: " + id + " for dashboard id: " + dashboardId));
        financialRecordRepository.delete(financialRecord);
    }
}