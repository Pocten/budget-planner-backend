package cz.cvut.fel.budgetplannerbackend.repository;

import cz.cvut.fel.budgetplannerbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUserEmail(String userEmail);

    Optional<User> findUserByUserName(String userName);

    @Query("SELECT u FROM User u WHERE u.userName = :usernameOrEmail OR u.userEmail = :usernameOrEmail")
    Optional<User> findUserByUserNameOrUserEmail(@Param("usernameOrEmail") String usernameOrEmail);

}
