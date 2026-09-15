package com.streakmate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Async
    public void sendReminderEmail(
            String toEmail,
            String username,
            String challengeTitle) {

        if (!mailEnabled) {
            log.info(
                    "[EMAIL-SKIPPED] Reminder to {} for '{}'",
                    toEmail,
                    challengeTitle
            );
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(
                    "StreakMate: Don't break your streak"
            );

            message.setText("""
                    Hi %s,

                    You haven't checked in today for your challenge:

                    %s

                    Take a moment to complete today's check-in
                    and keep your streak going.

                    Open StreakMate:
                    http://localhost:8080/dashboard

                    Keep going!

                    StreakMate
                    """.formatted(username, challengeTitle));

            mailSender.send(message);

            log.info(
                    "Reminder email sent to {}",
                    toEmail
            );

        } catch (Exception e) {
            log.error(
                    "Failed to send email to {}: {}",
                    toEmail,
                    e.getMessage()
            );
        }
    }

    @Async
    public void sendWeeklyReport(
            String toEmail,
            String username,
            int streak,
            double completion,
            int missed,
            int leaderboardRank) {

        if (!mailEnabled) {
            log.info(
                    "[EMAIL-SKIPPED] Weekly report to {}",
                    toEmail
            );
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(
                    "Your Weekly StreakMate Report"
            );

            message.setText("""
                    Hi %s,

                    Here's your StreakMate activity for this week.

                    Current streak: %d days
                    Completion rate: %.1f%%
                    Days missed: %d
                    Leaderboard rank: #%d

                    Open StreakMate:
                    http://localhost:8080/dashboard

                    Keep going!

                    StreakMate
                    """.formatted(
                    username,
                    streak,
                    completion,
                    missed,
                    leaderboardRank
            ));

            mailSender.send(message);

            log.info(
                    "Weekly report sent to {}",
                    toEmail
            );

        } catch (Exception e) {
            log.error(
                    "Failed to send weekly report to {}: {}",
                    toEmail,
                    e.getMessage()
            );
        }
    }

    @Async
    public void sendWelcomeEmail(
            String toEmail,
            String username) {

        if (!mailEnabled) {
            log.info(
                    "[EMAIL-SKIPPED] Welcome email to {}",
                    toEmail
            );
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(
                    "Welcome to StreakMate"
            );

            message.setText("""
                    Hi %s,

                    Welcome to StreakMate.

                    You can get started by:

                    - Creating a challenge
                    - Joining a challenge using its code
                    - Checking in every day

                    Open StreakMate:
                    http://localhost:8080/dashboard

                    Good luck with your challenges!

                    StreakMate
                    """.formatted(username));

            mailSender.send(message);

            log.info(
                    "Welcome email sent to {}",
                    toEmail
            );

        } catch (Exception e) {
            log.error(
                    "Failed to send welcome email to {}: {}",
                    toEmail,
                    e.getMessage()
            );
        }
    }
}