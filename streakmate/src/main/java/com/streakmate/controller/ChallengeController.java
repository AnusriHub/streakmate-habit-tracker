package com.streakmate.controller;

import com.streakmate.config.SessionConfig;
import com.streakmate.dto.CheckInDto;
import com.streakmate.dto.ChallengeCreateDto;
import com.streakmate.dto.LeaderboardEntryDto;
import com.streakmate.exception.DuplicateResourceException;
import com.streakmate.exception.InvalidOperationException;
import com.streakmate.exception.ResourceNotFoundException;
import com.streakmate.model.Challenge;
import com.streakmate.model.ChallengeParticipant;
import com.streakmate.service.BadgeService;
import com.streakmate.service.ChallengeService;
import com.streakmate.service.StreakService;
import com.streakmate.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;
    private final BadgeService badgeService;
    private final UserService userService;
    private final StreakService streakService;

    @GetMapping("/create")
    public String showCreate(HttpSession session, Model model) {

        if (session.getAttribute(SessionConfig.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }

        model.addAttribute("challengeDto", new ChallengeCreateDto());

        return "challenge/create";
    }

    @PostMapping("/create")
    public String createChallenge(
            @Valid @ModelAttribute("challengeDto") ChallengeCreateDto dto,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        Long userId = (Long) session.getAttribute(
                SessionConfig.SESSION_USER_ID
        );

        if (userId == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            return "challenge/create";
        }

        try {
            Challenge challenge = challengeService.createChallenge(dto, userId);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Challenge created! Share code: "
                            + challenge.getChallengeCode()
            );

            return "redirect:/challenges/" + challenge.getId();

        } catch (InvalidOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());

            return "challenge/create";
        }
    }

    @GetMapping("/join")
    public String showJoin(HttpSession session) {

        if (session.getAttribute(SessionConfig.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }

        return "challenge/join";
    }

    @PostMapping("/join")
    public String joinChallenge(
            @RequestParam String code,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute(
                SessionConfig.SESSION_USER_ID
        );

        if (userId == null) {
            return "redirect:/login";
        }

        String challengeCode = code.trim().toUpperCase();

        try {
            ChallengeParticipant participant =
                    challengeService.joinChallenge(challengeCode, userId);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "You joined the challenge!"
            );

            return "redirect:/challenges/"
                    + participant.getChallenge().getId();

        } catch (ResourceNotFoundException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Challenge code not found: " + challengeCode
            );

            return "redirect:/challenges/join";

        } catch (DuplicateResourceException | InvalidOperationException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            return "redirect:/challenges/join";
        }
    }

    @GetMapping("/{id}")
    public String challengeDetail(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        Long userId = (Long) session.getAttribute(
                SessionConfig.SESSION_USER_ID
        );

        if (userId == null) {
            return "redirect:/login";
        }

        Challenge challenge = challengeService.findById(id);

        List<LeaderboardEntryDto> leaderboard =
                challengeService.getLeaderboard(id);

        Optional<ChallengeParticipant> myParticipation =
                challengeService.findParticipant(userId, id);

        model.addAttribute("challenge", challenge);
        model.addAttribute("leaderboard", leaderboard);
        model.addAttribute(
                "myParticipation",
                myParticipation.orElse(null)
        );
        model.addAttribute(
                "isParticipant",
                myParticipation.isPresent()
        );
        model.addAttribute("checkInDto", new CheckInDto());

        if (myParticipation.isPresent()) {

            ChallengeParticipant participant =
                    myParticipation.get();

            model.addAttribute(
                    "hasCheckedInToday",
                    challengeService.hasCheckedInToday(
                            participant.getId()
                    )
            );

            model.addAttribute(
                    "currentStreak",
                    streakService.calculateCurrentStreak(
                            participant.getId()
                    )
            );

            model.addAttribute(
                    "completionPct",
                    challengeService.getCompletionPercentage(
                            participant.getId()
                    )
            );
        }

        return "challenge/detail";
    }

    @PostMapping("/{challengeId}/checkin")
    public String checkIn(
            @PathVariable Long challengeId,
            @ModelAttribute CheckInDto dto,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute(
                SessionConfig.SESSION_USER_ID
        );

        if (userId == null) {
            return "redirect:/login";
        }

        Optional<ChallengeParticipant> participant =
                challengeService.findParticipant(userId, challengeId);

        if (participant.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "You are not part of this challenge."
            );

            return "redirect:/challenges/" + challengeId;
        }

        try {
            challengeService.checkIn(
                    participant.get().getId(),
                    dto.getNote()
            );

            badgeService.evaluateAndAwardBadges(
                    userService.findById(userId)
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Checked in! Keep your streak going."
            );

        } catch (DuplicateResourceException | InvalidOperationException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/challenges/" + challengeId;
    }

    @GetMapping("/{id}/leaderboard")
    public String leaderboard(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        if (session.getAttribute(SessionConfig.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "challenge",
                challengeService.findById(id)
        );

        model.addAttribute(
                "leaderboard",
                challengeService.getLeaderboard(id)
        );

        return "challenge/leaderboard";
    }

    @GetMapping("/history")
    public String history(
            HttpSession session,
            Model model) {

        Long userId = (Long) session.getAttribute(
                SessionConfig.SESSION_USER_ID
        );

        if (userId == null) {
            return "redirect:/login";
        }

        List<ChallengeParticipant> participations =
                challengeService.getParticipationsByUser(userId);

        model.addAttribute("participations", participations);
        model.addAttribute(
                "user",
                userService.findById(userId)
        );

        return "challenge/history";
    }
}