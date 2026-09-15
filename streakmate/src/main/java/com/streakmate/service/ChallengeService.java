package com.streakmate.service;

import com.streakmate.dto.ChallengeCreateDto;
import com.streakmate.dto.LeaderboardEntryDto;
import com.streakmate.exception.DuplicateResourceException;
import com.streakmate.exception.InvalidOperationException;
import com.streakmate.exception.ResourceNotFoundException;
import com.streakmate.model.Challenge;
import com.streakmate.model.ChallengeParticipant;
import com.streakmate.model.DailyCheckIn;
import com.streakmate.model.User;
import com.streakmate.repository.ChallengeParticipantRepository;
import com.streakmate.repository.ChallengeRepository;
import com.streakmate.repository.DailyCheckInRepository;
import com.streakmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipantRepository participantRepository;
    private final DailyCheckInRepository checkInRepository;
    private final UserRepository userRepository;
    private final StreakService streakService;

    @Transactional
    public Challenge createChallenge(
            ChallengeCreateDto dto,
            Long creatorId) {

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new InvalidOperationException(
                    "End date must be after start date"
            );
        }

        if (dto.getStartDate().isBefore(LocalDate.now())) {
            throw new InvalidOperationException(
                    "Start date cannot be in the past"
            );
        }

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        String code = generateChallengeCode(
                dto.getTitle(),
                dto.getEndDate(),
                dto.getStartDate()
        );

        Challenge challenge = Challenge.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .challengeCode(code)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .creator(creator)
                .status(Challenge.ChallengeStatus.ACTIVE)
                .build();

        Challenge savedChallenge =
                challengeRepository.save(challenge);

        // The creator automatically joins the challenge.
        joinChallenge(savedChallenge.getChallengeCode(), creatorId);

        log.info(
                "Challenge created: {} [{}] by {}",
                savedChallenge.getTitle(),
                savedChallenge.getChallengeCode(),
                creator.getUsername()
        );

        return savedChallenge;
    }

    @Transactional
    public ChallengeParticipant joinChallenge(
            String code,
            Long userId) {

        String challengeCode = code.toUpperCase();

        Challenge challenge =
                challengeRepository.findByChallengeCode(challengeCode)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Challenge not found with code: "
                                                + code
                                )
                        );

        if (challenge.getStatus() == Challenge.ChallengeStatus.EXPIRED
                || challenge.getStatus()
                == Challenge.ChallengeStatus.ARCHIVED) {

            throw new InvalidOperationException(
                    "This challenge is no longer active."
            );
        }

        if (participantRepository.existsByUserIdAndChallengeId(
                userId,
                challenge.getId())) {

            throw new DuplicateResourceException(
                    "You are already part of this challenge."
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        ChallengeParticipant participant =
                ChallengeParticipant.builder()
                        .user(user)
                        .challenge(challenge)
                        .build();

        return participantRepository.save(participant);
    }

    @Transactional
    public DailyCheckIn checkIn(
            Long participantId,
            String note) {

        ChallengeParticipant participant =
                participantRepository.findById(participantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Participation not found"
                                )
                        );

        LocalDate today = LocalDate.now();

        if (checkInRepository
                .existsByParticipantIdAndCheckInDate(
                        participantId,
                        today
                )) {

            throw new DuplicateResourceException(
                    "You have already checked in today!"
            );
        }

        Challenge challenge = participant.getChallenge();

        if (today.isBefore(challenge.getStartDate())
                || today.isAfter(challenge.getEndDate())) {

            throw new InvalidOperationException(
                    "Check-in is only allowed during the challenge period."
            );
        }

        DailyCheckIn checkIn = DailyCheckIn.builder()
                .participant(participant)
                .checkInDate(today)
                .note(note)
                .completed(true)
                .build();

        return checkInRepository.save(checkIn);
    }

    public List<LeaderboardEntryDto> getLeaderboard(
            Long challengeId) {

        List<ChallengeParticipant> participants =
                participantRepository.findByChallengeId(challengeId);

        Challenge challenge =
                challengeRepository.findById(challengeId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Challenge not found"
                                )
                        );

        List<LeaderboardEntryDto> entries = new ArrayList<>();

        for (ChallengeParticipant participant : participants) {

            long completed =
                    checkInRepository.countCompletedByParticipantId(
                            participant.getId()
                    );

            int currentStreak =
                    streakService.calculateCurrentStreak(
                            participant.getId()
                    );

            int longestStreak =
                    streakService.calculateLongestStreak(
                            participant.getId()
                    );

            double completion =
                    challenge.getTotalDays() > 0
                            ? (double) completed
                            / challenge.getTotalDays()
                            * 100
                            : 0;

            entries.add(
                    LeaderboardEntryDto.builder()
                            .username(
                                    participant.getUser().getUsername()
                            )
                            .name(
                                    participant.getUser().getName()
                            )
                            .currentStreak(currentStreak)
                            .longestStreak(longestStreak)
                            .completionPercentage(
                                    Math.min(100, completion)
                            )
                            .totalCheckIns((int) completed)
                            .build()
            );
        }

        entries.sort(
                Comparator.comparingDouble(
                                LeaderboardEntryDto::getCompletionPercentage
                        ).reversed()
                        .thenComparing(
                                Comparator.comparingInt(
                                        LeaderboardEntryDto::getCurrentStreak
                                ).reversed()
                        )
        );

        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setRank(i + 1);
        }

        return entries;
    }

    public double getCompletionPercentage(Long participantId) {

        ChallengeParticipant participant =
                participantRepository.findById(participantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Participation not found"
                                )
                        );

        Challenge challenge = participant.getChallenge();

        long completed =
                checkInRepository.countCompletedByParticipantId(
                        participantId
                );

        if (challenge.getTotalDays() <= 0) {
            return 0;
        }

        return Math.min(
                100,
                (double) completed
                        / challenge.getTotalDays()
                        * 100
        );
    }

    public Challenge findByCode(String code) {

        return challengeRepository
                .findByChallengeCode(code.toUpperCase())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Challenge not found with code: " + code
                        )
                );
    }

    public Challenge findById(Long id) {

        return challengeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Challenge not found"
                        )
                );
    }

    public List<ChallengeParticipant> getParticipationsByUser(
            Long userId) {

        return participantRepository.findByUserId(userId);
    }

    public List<ChallengeParticipant> getActiveParticipationsByUser(
            Long userId) {

        return participantRepository.findActiveByUserId(userId);
    }

    public Optional<ChallengeParticipant> findParticipant(
            Long userId,
            Long challengeId) {

        return participantRepository
                .findByUserIdAndChallengeId(
                        userId,
                        challengeId
                );
    }

    public List<DailyCheckIn> getRecentActivity(Long userId) {

        return checkInRepository
                .findAllByUserId(userId)
                .stream()
                .limit(10)
                .toList();
    }

    public boolean hasCheckedInToday(Long participantId) {

        return checkInRepository
                .existsByParticipantIdAndCheckInDate(
                        participantId,
                        LocalDate.now()
                );
    }

    @Transactional
    public void expireOldChallenges() {

        List<Challenge> expired =
                challengeRepository.findExpiredChallenges(
                        LocalDate.now()
                );

        expired.forEach(
                challenge ->
                        challenge.setStatus(
                                Challenge.ChallengeStatus.EXPIRED
                        )
        );

        challengeRepository.saveAll(expired);

        log.info(
                "Expired {} challenges",
                expired.size()
        );
    }

    private String generateChallengeCode(
            String title,
            LocalDate endDate,
            LocalDate startDate) {

        int days =
                (int) (
                        endDate.toEpochDay()
                                - startDate.toEpochDay()
                                + 1
                );

        String prefix =
                title.replaceAll("[^a-zA-Z]", "")
                        .toUpperCase();

        if (prefix.length() > 8) {
            prefix = prefix.substring(0, 8);
        }

        String base = prefix + days;
        String code = base;
        int attempt = 0;

        while (challengeRepository.existsByChallengeCode(code)) {
            attempt++;
            code = base + attempt;
        }

        return code;
    }
}