package cz.cvut.fel.budgetplannerbackend.repository;

import cz.cvut.fel.budgetplannerbackend.entity.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, String> {

    List<FinancialRecord> findAllByDashboardId(Long dashboardId);

    Optional<FinancialRecord> findByIdAndDashboardId(Long id, Long dashboardId);
}
