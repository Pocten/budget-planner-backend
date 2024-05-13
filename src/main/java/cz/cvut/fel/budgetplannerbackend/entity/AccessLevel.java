package cz.cvut.fel.budgetplannerbackend.entity;

import cz.cvut.fel.budgetplannerbackend.entity.enums.EAccessLevel;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "access_levels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccessLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", unique = true, nullable = false)
    private EAccessLevel level;
}
