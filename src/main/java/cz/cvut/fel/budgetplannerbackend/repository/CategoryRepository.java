package cz.cvut.fel.budgetplannerbackend.repository;

import cz.cvut.fel.budgetplannerbackend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByDashboardId(Long dashboardId);

    Optional<Category> findByIdAndDashboardId(Long id, Long dashboardId);

    @Modifying
    @Query("DELETE FROM Category c WHERE c.dashboard.id = :dashboardId")
    void deleteByDashboardId(@Param("dashboardId") Long dashboardId);
}
