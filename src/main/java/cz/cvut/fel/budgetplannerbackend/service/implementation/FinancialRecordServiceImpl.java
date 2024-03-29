package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.dto.FinancialRecordDto;
import cz.cvut.fel.budgetplannerbackend.entity.Category;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import cz.cvut.fel.budgetplannerbackend.entity.FinancialRecord;
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
        List<FinancialRecord> records = financialRecordRepository.findAllByDashboardId(dashboardId);
        return records.stream().map(financialRecordMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialRecordDto findByIdAndDashboardId(Long id, Long dashboardId) {
        LOG.info("Fetching financial record with id: {} for dashboard id: {}", id, dashboardId);
        FinancialRecord record = financialRecordRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("FinancialRecord", id));
        return financialRecordMapper.toDto(record);
    }

    @Override
    @Transactional
    public FinancialRecordDto create(Long dashboardId, FinancialRecordDto financialRecordDto) {
        LOG.info("Creating financial record for dashboard id: {}", dashboardId);
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard", dashboardId));
        FinancialRecord record = financialRecordMapper.toEntity(financialRecordDto);
        record.setDashboard(dashboard);
        FinancialRecord savedRecord = financialRecordRepository.save(record);
        return financialRecordMapper.toDto(savedRecord);
    }

    @Override
    @Transactional
    public FinancialRecordDto update(Long id, Long dashboardId, FinancialRecordDto financialRecordDto) {
        LOG.info("Updating financial record with id: {} for dashboard id: {}", id, dashboardId);
        FinancialRecord record = financialRecordRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("FinancialRecord", id));

        if (financialRecordDto.amount() != null) {
            record.setAmount(financialRecordDto.amount());
        }
        if (financialRecordDto.description() != null) {
            record.setDescription(financialRecordDto.description());
        }
        if (financialRecordDto.date() != null) {
            record.setDate(financialRecordDto.date());
        }
        if (financialRecordDto.categoryId() != null) {
            Category category = categoryRepository.findById(financialRecordDto.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category", financialRecordDto.categoryId()));
            record.setCategory(category);
        }

        FinancialRecord updatedRecord = financialRecordRepository.save(record);
        LOG.info("Updated financial record with id: {} for dashboard id: {}", id, dashboardId);
        return financialRecordMapper.toDto(updatedRecord);
    }
    @Override
    @Transactional
    public void delete(Long id, Long dashboardId) {
        LOG.info("Deleting financial record with id: {} for dashboard id: {}", id, dashboardId);
        FinancialRecord record = financialRecordRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("FinancialRecord", id));
        financialRecordRepository.delete(record);
    }
}
