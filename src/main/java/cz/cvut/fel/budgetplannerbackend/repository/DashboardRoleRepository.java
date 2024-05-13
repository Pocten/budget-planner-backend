package cz.cvut.fel.budgetplannerbackend.repository;

import cz.cvut.fel.budgetplannerbackend.entity.DashboardRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface DashboardRoleRepository extends JpaRepository<DashboardRole, Long> {

    @Query("SELECT dr FROM DashboardRole dr JOIN FETCH dr.role WHERE dr.user.id = :userId AND dr.dashboard.id = :dashboardId")
    Optional<DashboardRole> findByUserIdAndDashboardId(@Param("userId") Long userId, @Param("dashboardId") Long dashboardId);

    List<DashboardRole> findAllByDashboardId(Long dashboardId);

    @Modifying
    @Transactional
    @Query("DELETE FROM DashboardRole dr WHERE dr.dashboard.id = :dashboardId")
    void deleteByDashboardId(@Param("dashboardId") Long dashboardId);

}
