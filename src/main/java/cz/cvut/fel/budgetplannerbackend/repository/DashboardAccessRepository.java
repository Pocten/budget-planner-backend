package cz.cvut.fel.budgetplannerbackend.repository;

import cz.cvut.fel.budgetplannerbackend.entity.DashboardAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface DashboardAccessRepository extends JpaRepository<DashboardAccess, Long> {

    Optional<DashboardAccess> findByUserIdAndDashboardId(Long userId, Long dashboardId);

    List<DashboardAccess> findAllByDashboardId(Long dashboardId);

    List<DashboardAccess> findAllByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM DashboardAccess da WHERE da.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM DashboardAccess da WHERE da.dashboard.id = :dashboardId")
    void deleteByDashboardId(@Param("dashboardId") Long dashboardId);

}