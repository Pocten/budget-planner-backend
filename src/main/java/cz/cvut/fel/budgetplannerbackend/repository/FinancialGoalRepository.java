package cz.cvut.fel.budgetplannerbackend.repository;

import cz.cvut.fel.budgetplannerbackend.entity.FinancialGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, Long> {

    List<FinancialGoal> findByDashboardId(Long dashboardId);

    Optional<FinancialGoal> findByIdAndDashboardId(Long goalId, Long dashboardId);

    void deleteByDashboardId(Long dashboardId);
}