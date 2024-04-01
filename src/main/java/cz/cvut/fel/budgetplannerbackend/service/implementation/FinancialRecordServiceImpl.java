package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.dto.FinancialRecordDto;
import cz.cvut.fel.budgetplannerbackend.entity.Category;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import cz.cvut.fel.budgetplannerbackend.entity.FinancialRecord;
import cz.cvut.fel.budgetplannerbackend.entity.enums.ERecordType;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.FinancialRecordMapper;
import cz.cvut.fel.budgetplannerbackend.repository.CategoryRepository;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRepository;
import cz.cvut.fel.budgetplannerbackend.repository.FinancialRecordRepository;
import cz.cvut.fel.budgetplannerbackend.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private final FinancialRecordRepository financialRecordRepository;
    private final DashboardRepository dashboardRepository;
    private final FinancialRecordMapper financialRecordMapper;
    private final CategoryRepository categoryRepository;

    private static final Logger LOG = LoggerFactory.getLogger(FinancialRecordServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public List<FinancialRecordDto> findAllByDashboardId(Long dashboardId) {
        LOG.info("Fetching all financial records for dashboard id: {}", dashboardId);
        List<FinancialRecord> financialRecords = financialRecordRepository.findAllByDashboardId(dashboardId);
        return financialRecords.stream()
                .map(financialRecordMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialRecordDto findByIdAndDashboardId(Long id, Long dashboardId) {
        LOG.info("Fetching financial record with id: {} for dashboard id: {}", id, dashboardId);
        FinancialRecord financialRecord = financialRecordRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("FinancialRecord", id));
        return financialRecordMapper.toDto(financialRecord);
    }

    @Override
    @Transactional
    public FinancialRecordDto create(Long dashboardId, FinancialRecordDto financialRecordDto) {
        LOG.info("Creating financial record for dashboard id: {}", dashboardId);
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard", dashboardId));

        Category category = null;
        if (financialRecordDto.category().id() != null) {
            category = categoryRepository.findById(financialRecordDto.category().id())
                    .orElseThrow(() -> new EntityNotFoundException("Category", financialRecordDto.category().id()));
        }

        FinancialRecord financialRecord = new FinancialRecord();
        financialRecord.setDashboard(dashboard);
        financialRecord.setAmount(financialRecordDto.amount());
        financialRecord.setCategory(category);
        financialRecord.setType(financialRecordDto.type() != null ? financialRecordDto.type() : ERecordType.INCOME);
        financialRecord.setDate(financialRecordDto.date() != null ? financialRecordDto.date() : LocalDateTime.now());
        financialRecord.setDescription(financialRecordDto.description());

        FinancialRecord savedRecord = financialRecordRepository.save(financialRecord);
        return financialRecordMapper.toDto(savedRecord);
    }

    @Override
    @Transactional
    public FinancialRecordDto update(Long id, Long dashboardId, FinancialRecordDto financialRecordDto) {
        LOG.info("Updating financial record with id: {} for dashboard id: {}", id, dashboardId);
        FinancialRecord financialRecord = financialRecordRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("FinancialRecord", id));

        financialRecord.setAmount(financialRecordDto.amount());
        financialRecord.setDescription(financialRecordDto.description());
        financialRecord.setDate(financialRecordDto.date() != null ? financialRecordDto.date() : financialRecord.getDate());
        financialRecord.setType(financialRecordDto.type() != null ? financialRecordDto.type() : financialRecord.getType());

        if (financialRecordDto.category().id() != null) {
            Category category = categoryRepository.findById(financialRecordDto.category().id())
                    .orElseThrow(() -> new EntityNotFoundException("Category", financialRecordDto.category().id()));
            financialRecord.setCategory(category);
        } else {
            financialRecord.setCategory(null);
        }

        FinancialRecord updatedRecord = financialRecordRepository.save(financialRecord);
        LOG.info("Updated financial record with id: {} for dashboard id: {}", id, dashboardId);
        return financialRecordMapper.toDto(updatedRecord);
    }
    @Override
    @Transactional
    public void delete(Long id, Long dashboardId) {
        LOG.info("Deleting financial record with id: {} for dashboard id: {}", id, dashboardId);
        FinancialRecord financialRecord = financialRecordRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("FinancialRecord", id));
        financialRecordRepository.delete(financialRecord);
    }
}
