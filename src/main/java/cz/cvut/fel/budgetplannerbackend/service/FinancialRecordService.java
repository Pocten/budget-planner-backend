package cz.cvut.fel.budgetplannerbackend.service;

import cz.cvut.fel.budgetplannerbackend.dto.FinancialRecordDto;

import java.util.List;

public interface FinancialRecordService {
    List<FinancialRecordDto> findAllFinancialRecordsByDashboardId(Long dashboardId);

    FinancialRecordDto findFinancialRecordByIdAndDashboardId(Long id, Long dashboardId);

    FinancialRecordDto createFinancialRecord(Long dashboardId, FinancialRecordDto financialRecordDto);

    FinancialRecordDto updateFinancialRecord(Long dashboardId, Long id, FinancialRecordDto financialRecordDto);

    void deleteFinancialRecord(Long dashboardId, Long id);
}

