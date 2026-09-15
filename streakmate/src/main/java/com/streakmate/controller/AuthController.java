package com.streakmate.controller;

import com.streakmate.config.SessionConfig;
import com.streakmate.dto.UserRegistrationDto;
import com.streakmate.exception.DuplicateResourceException;
import com.streakmate.model.User;
import com.streakmate.service.EmailService;
import com.streakmate.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;

    @GetMapping("/")
    public String landing(HttpSession session) {

        if (session.getAttribute(SessionConfig.SESSION_USER_ID) != null) {
            return "redirect:/dashboard";
        }

        return "landing";
    }

    @GetMapping("/register")
    public String showRegister(Model model, HttpSession session) {

        if (session.getAttribute(SessionConfig.SESSION_USER_ID) != null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("userDto", new UserRegistrationDto());

        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("userDto") UserRegistrationDto dto,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            User user = userService.register(dto);

            session.setAttribute(
                    SessionConfig.SESSION_USER_ID,
                    user.getId()
            );

            session.setAttribute(
                    SessionConfig.SESSION_USERNAME,
                    user.getUsername()
            );

            emailService.sendWelcomeEmail(
                    user.getEmail(),
                    user.getUsername()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Welcome to StreakMate, " + user.getName() + "!"
            );

            return "redirect:/dashboard";

        } catch (DuplicateResourceException e) {

            model.addAttribute("errorMessage", e.getMessage());

            return "auth/register";
        }
    }

    @GetMapping("/login")
    public String showLogin(Model model, HttpSession session) {

        if (session.getAttribute(SessionConfig.SESSION_USER_ID) != null) {
            return "redirect:/dashboard";
        }

        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String identifier,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String value = identifier.trim().toLowerCase();

        try {
            User user;

            if (value.contains("@")) {
                user = userService.findByEmail(value);
            } else {
                user = userService.findByUsername(value);
            }

            session.setAttribute(
                    SessionConfig.SESSION_USER_ID,
                    user.getId()
            );

            session.setAttribute(
                    SessionConfig.SESSION_USERNAME,
                    user.getUsername()
            );

            return "redirect:/dashboard";

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "No account found. Please register first."
            );

            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }
}