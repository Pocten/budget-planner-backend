package cz.cvut.fel.budgetplannerbackend.controller;

import cz.cvut.fel.budgetplannerbackend.dto.FinancialRecordDto;
import cz.cvut.fel.budgetplannerbackend.service.implementation.FinancialRecordServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboards/{dashboardId}/financial-records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordServiceImpl financialRecordService;
    private static final Logger LOG = LoggerFactory.getLogger(FinancialRecordController.class);

    @GetMapping
    public ResponseEntity<List<FinancialRecordDto>> getAllFinancialRecordsByDashboardId(@PathVariable Long dashboardId) {
        LOG.info("Received request to list all financial records for dashboard id: {}", dashboardId);
        List<FinancialRecordDto> records = financialRecordService.findAllByDashboardId(dashboardId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancialRecordDto> getFinancialRecordByIdAndDashboardId(@PathVariable Long dashboardId, @PathVariable Long id) {
        LOG.info("Received request to fetch financial record with id: {} for dashboard id: {}", id, dashboardId);
        FinancialRecordDto recordDto = financialRecordService.findByIdAndDashboardId(id, dashboardId);
        return ResponseEntity.ok(recordDto);
    }

    @PostMapping
    public ResponseEntity<FinancialRecordDto> createFinancialRecord(@PathVariable Long dashboardId, @RequestBody FinancialRecordDto financialRecordDto) {
        LOG.info("Received request to create a new financial record for dashboard id: {}", dashboardId);
        FinancialRecordDto createdRecord = financialRecordService.create(dashboardId, financialRecordDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecord);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinancialRecordDto> updateFinancialRecord(@PathVariable Long dashboardId, @PathVariable Long id, @RequestBody FinancialRecordDto financialRecordDto) {
        LOG.info("Received request to update financial record with id: {} for dashboard id: {}", id, dashboardId);
        FinancialRecordDto updatedRecord = financialRecordService.update(id, dashboardId, financialRecordDto);
        return ResponseEntity.ok(updatedRecord);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFinancialRecord(@PathVariable Long dashboardId, @PathVariable Long id) {
        LOG.info("Received request to delete financial record with id: {} for dashboard id: {}", id, dashboardId);
        financialRecordService.delete(id, dashboardId);
        return ResponseEntity.noContent().build();
    }
}
