package cz.cvut.fel.budgetplannerbackend.service;

import cz.cvut.fel.budgetplannerbackend.dto.FinancialRecordDto;

import java.util.List;

public interface FinancialRecordService {
    List<FinancialRecordDto> findAllByDashboardId(Long dashboardId);

    FinancialRecordDto findByIdAndDashboardId(Long id, Long dashboardId);

    FinancialRecordDto create(Long dashboardId, FinancialRecordDto financialRecordDto);

    FinancialRecordDto update(Long id, Long dashboardId, FinancialRecordDto financialRecordDto);

    void delete(Long id, Long dashboardId);
}
