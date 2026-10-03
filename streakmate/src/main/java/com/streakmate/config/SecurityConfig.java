package com.streakmate.config;

import com.streakmate.model.User;
import com.streakmate.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Piece 1: how to find a user from what they typed (email or username)
    @Bean
    public UserDetailsService userDetailsService() {
        return identifier -> {
            String value = identifier.trim().toLowerCase();

            User user = (value.contains("@")
                    ? userRepository.findByEmail(value)
                    : userRepository.findByUsername(value))
                    .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));

            if (user.getPassword() == null) {
                // account made before passwords existed
                throw new UsernameNotFoundException("Invalid credentials");
            }

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
        };
    }

    // Piece 3: after a correct login, fill the session like the old code did
    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return (request, response, authentication) -> {
            User user = userRepository.findByUsername(authentication.getName()).orElseThrow();

            HttpSession session = request.getSession();
            session.setAttribute(SessionConfig.SESSION_USER_ID, user.getId());
            session.setAttribute(SessionConfig.SESSION_USERNAME, user.getUsername());

            response.sendRedirect(request.getContextPath() + "/dashboard");
        };
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register",
                                "/css/**", "/js/**", "/error", "/error/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("identifier")
                        .passwordParameter("password")
                        .successHandler(loginSuccessHandler())
                        .failureUrl("/login?error"))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .exceptionHandling(ex -> ex
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                request -> request.getRequestURI().startsWith("/api/")));
        return http.build();
    }
}