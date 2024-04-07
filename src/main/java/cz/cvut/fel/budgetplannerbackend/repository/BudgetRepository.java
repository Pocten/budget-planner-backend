package cz.cvut.fel.budgetplannerbackend.repository;

import cz.cvut.fel.budgetplannerbackend.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findAllByDashboardId(Long dashboardId);

    Optional<Budget> findByIdAndDashboardId(Long id, Long dashboardId);

    @Modifying
    @Query("DELETE FROM Budget b WHERE b.dashboard.id = :dashboardId")
    void deleteByDashboardId(@Param("dashboardId") Long dashboardId);
}

