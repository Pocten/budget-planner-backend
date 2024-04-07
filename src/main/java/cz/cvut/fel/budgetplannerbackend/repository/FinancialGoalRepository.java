package cz.cvut.fel.budgetplannerbackend.repository;

import cz.cvut.fel.budgetplannerbackend.entity.FinancialGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, Long> {

    List<FinancialGoal> findByBudgetId(Long budgetId);

    Optional<FinancialGoal> findByIdAndBudgetId(Long goalId, Long budgetId);

    void deleteByBudgetId(Long budgetId);
}


