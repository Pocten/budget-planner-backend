package cz.cvut.fel.budgetplannerbackend.repository;

import cz.cvut.fel.budgetplannerbackend.entity.InviteLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InviteLinkRepository extends JpaRepository<InviteLink, Long> {

    List<InviteLink> findByDashboardId(Long dashboardId);

    Optional<InviteLink> findByLink(String link);

    @Query("SELECT l FROM InviteLink l WHERE l.expiryDate < :currentTime AND l.active = true")
    List<InviteLink> findExpiredLinks(LocalDateTime currentTime);

    @Query("SELECT l FROM InviteLink l WHERE l.link = :link AND l.active = true")
    Optional<InviteLink> findByLinkAndIsActiveTrue(String link);

    @Query("SELECT i FROM InviteLink i WHERE i.dashboard.id = :dashboardId AND i.active = true")
    Optional<InviteLink> findByDashboardIdAndIsActiveTrue(Long dashboardId);
}

