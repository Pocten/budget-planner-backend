package cz.cvut.fel.budgetplannerbackend.repository;

import cz.cvut.fel.budgetplannerbackend.entity.CategoryPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryPriorityRepository extends JpaRepository<CategoryPriority, Long> {

    List<CategoryPriority> findByCategoryIdAndDashboardId(Long categoryId, Long dashboardId);

    List<CategoryPriority> findByDashboardId(Long dashboardId);
}
