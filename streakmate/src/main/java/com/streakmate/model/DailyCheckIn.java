package com.streakmate.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_check_ins",
        uniqueConstraints = @UniqueConstraint(columnNames = {"participant_id", "check_in_date"}))
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyCheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private ChallengeParticipant participant;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(length = 300)
    private String note;

    @Column(nullable = false)
    @Builder.Default
    private Boolean completed = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
