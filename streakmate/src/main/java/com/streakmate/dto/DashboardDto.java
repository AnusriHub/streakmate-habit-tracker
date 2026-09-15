package com.streakmate.dto;

import com.streakmate.model.ChallengeParticipant;
import com.streakmate.model.DailyCheckIn;
import com.streakmate.model.UserBadge;
import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDto {
    private int currentStreak;
    private int longestStreak;
    private double completionPercentage;
    private int activeChallenges;
    private int completedChallenges;
    private int totalCheckIns;
    private List<ChallengeParticipant> activeParticipations;
    private List<DailyCheckIn> recentActivity;
    private List<UserBadge> badges;
}
