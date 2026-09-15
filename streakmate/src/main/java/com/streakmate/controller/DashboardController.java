package com.streakmate.controller;

import com.streakmate.config.SessionConfig;
import com.streakmate.dto.DashboardDto;
import com.streakmate.service.DashboardService;
import com.streakmate.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute(
                SessionConfig.SESSION_USER_ID
        );

        if (userId == null) {
            return "redirect:/login";
        }

        DashboardDto dashboard =
                dashboardService.buildDashboard(userId);

        model.addAttribute(
                "user",
                userService.findById(userId)
        );

        model.addAttribute(
                "dashboard",
                dashboard
        );

        return "dashboard/dashboard";
    }
}