package cz.cvut.fel.budgetplannerbackend.repository;

import cz.cvut.fel.budgetplannerbackend.entity.FinancialRecord;
import cz.cvut.fel.budgetplannerbackend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, String> {

    List<FinancialRecord> findAllByDashboardId(Long dashboardId);

    Optional<FinancialRecord> findByIdAndDashboardId(Long id, Long dashboardId);

    @Modifying
    @Query("UPDATE FinancialRecord fr SET fr.category = null WHERE fr.category.id = :categoryId")
    void setCategoryToNullByCategoryId(@Param("categoryId") Long categoryId);

    @Modifying
    @Query("DELETE FROM FinancialRecord fr WHERE fr.dashboard.id = :dashboardId")
    void deleteByDashboardId(@Param("dashboardId") Long dashboardId);

    @Query("SELECT fr FROM FinancialRecord fr JOIN fr.tags t WHERE t = :tag")
    List<FinancialRecord> findAllWithTag(@Param("tag") Tag tag);
}
