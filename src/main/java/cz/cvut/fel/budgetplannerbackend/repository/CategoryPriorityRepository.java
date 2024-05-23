package cz.cvut.fel.budgetplannerbackend.repository;

import cz.cvut.fel.budgetplannerbackend.entity.CategoryPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryPriorityRepository extends JpaRepository<CategoryPriority, Long> {

    List<CategoryPriority> findByCategoryIdAndDashboardId(Long categoryId, Long dashboardId);

    List<CategoryPriority> findByDashboardId(Long dashboardId);

    List<CategoryPriority> findByUserIdAndDashboardId(Long userId, Long dashboardId);

    Optional<CategoryPriority> findByUserIdAndCategoryIdAndDashboardId(Long userId, Long categoryId, Long dashboardId);

    void deleteByCategoryId (Long categoryId);

    @Modifying
    @Query("DELETE FROM CategoryPriority cp WHERE cp.dashboard.id = :dashboardId")
    void deleteByDashboardId(@Param("dashboardId") Long dashboardId);
}
