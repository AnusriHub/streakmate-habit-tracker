package com.streakmate.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "badges")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String badgeName;

    @Column(length = 200)
    private String description;

    @Column(length = 10)
    private String icon;

    @Column(nullable = false)
    private Integer threshold; // check-ins or streak days required

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BadgeType type;

    public enum BadgeType {
        CHECK_IN_COUNT, STREAK_DAYS, CHALLENGE_COMPLETE
    }
}
