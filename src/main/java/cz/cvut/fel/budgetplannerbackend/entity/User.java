package cz.cvut.fel.budgetplannerbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_name", unique = true, nullable = false)
    @NotBlank(message = "User name is required")
    private String userName;

    @Column(name = "user_email", unique = true, nullable = false)
    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String userEmail;

    @Column(name = "user_password", nullable = false)
    @NotBlank(message = "Password is required")
    private String userPassword;

    @CreationTimestamp
    @Column(name = "user_date_registration", nullable = false, updatable = false)
    private LocalDateTime userDateRegistration;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}
