package com.streakmate.service;

import com.streakmate.dto.UserRegistrationDto;
import com.streakmate.exception.DuplicateResourceException;
import com.streakmate.model.User;
import com.streakmate.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService – registration logic")
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks UserService userService;

    @Test
    @DisplayName("Registers a new user successfully")
    void register_success() {
        UserRegistrationDto dto = new UserRegistrationDto("Alice", "alice@test.com", "alice_j", "password123");
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("alice_j")).thenReturn(false);

        User saved = User.builder().id(1L).name("Alice").email("alice@test.com").username("alice_j").build();
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userService.register(dto);
        assertThat(result.getEmail()).isEqualTo("alice@test.com");
        assertThat(result.getUsername()).isEqualTo("alice_j");
    }

    @Test
    @DisplayName("Throws on duplicate email")
    void register_duplicateEmail_throws() {
        UserRegistrationDto dto = new UserRegistrationDto("Bob", "taken@test.com", "bob", "password123");
        when(userRepository.existsByEmail("taken@test.com")).thenReturn(true);
        assertThatThrownBy(() -> userService.register(dto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email");
    }

    @Test
    @DisplayName("Throws on duplicate username")
    void register_duplicateUsername_throws() {
        UserRegistrationDto dto = new UserRegistrationDto("Bob", "bob@test.com", "taken", "password123");
        when(userRepository.existsByEmail("bob@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("taken")).thenReturn(true);
        assertThatThrownBy(() -> userService.register(dto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Username");
    }
}