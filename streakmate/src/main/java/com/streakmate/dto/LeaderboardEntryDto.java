package com.streakmate.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardEntryDto {
    private int rank;
    private String username;
    private String name;
    private int currentStreak;
    private int longestStreak;
    private double completionPercentage;
    private int totalCheckIns;
}
