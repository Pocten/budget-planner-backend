package cz.cvut.fel.budgetplannerbackend.repository;

import cz.cvut.fel.budgetplannerbackend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findAllByDashboardId(Long dashboardId);

    Optional<Tag> findByIdAndDashboardId(Long id, Long dashboardId);

    @Modifying
    @Query("DELETE FROM Tag t WHERE t.dashboard.id = :dashboardId")
    void deleteByDashboardId(@Param("dashboardId") Long dashboardId);
}

