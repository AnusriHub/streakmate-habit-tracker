package com.streakmate.controller;

import com.streakmate.config.SessionConfig;
import com.streakmate.dto.LeaderboardEntryDto;
import com.streakmate.model.Challenge;
import com.streakmate.model.DailyCheckIn;
import com.streakmate.model.User;
import com.streakmate.service.ChallengeService;
import com.streakmate.service.StreakService;
import com.streakmate.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ApiController {

    private final UserService userService;
    private final ChallengeService challengeService;
    private final StreakService streakService;

    // NOTE: the old GET /users (list of every user) was removed on purpose.

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(
            @PathVariable Long id,
            HttpSession session) {

        if (!isSelf(id, session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/challenges/{id}")
    public ResponseEntity<Challenge> getChallenge(@PathVariable Long id) {
        return ResponseEntity.ok(challengeService.findById(id));
    }

    @GetMapping("/challenges/{id}/leaderboard")
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                challengeService.getLeaderboard(id)
        );
    }

    @GetMapping("/challenges/code/{code}")
    public ResponseEntity<Challenge> getChallengeByCode(
            @PathVariable String code) {

        return ResponseEntity.ok(
                challengeService.findByCode(code)
        );
    }

    @GetMapping("/users/{id}/activity")
    public ResponseEntity<List<DailyCheckIn>> getActivity(
            @PathVariable Long id,
            HttpSession session) {

        if (!isSelf(id, session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(
                challengeService.getRecentActivity(id)
        );
    }

    @GetMapping("/users/{id}/streak")
    public ResponseEntity<Map<String, Integer>> getStreak(
            @PathVariable Long id,
            HttpSession session) {

        if (!isSelf(id, session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        int currentStreak =
                streakService.calculateGlobalCurrentStreak(id);

        int longestStreak =
                streakService.calculateGlobalLongestStreak(id);

        Map<String, Integer> result = Map.of(
                "currentStreak", currentStreak,
                "longestStreak", longestStreak
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/session/user")
    public ResponseEntity<?> getSessionUser(HttpSession session) {

        Long userId = (Long) session.getAttribute(
                SessionConfig.SESSION_USER_ID
        );

        if (userId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Not logged in"));
        }

        User user = userService.findById(userId);

        return ResponseEntity.ok(user);
    }

    /**
     * True only when the id in the URL belongs to the logged-in user.
     * This is what stops user 5 from reading user 6's data.
     */
    private boolean isSelf(Long id, HttpSession session) {
        return id.equals(session.getAttribute(SessionConfig.SESSION_USER_ID));
    }
}