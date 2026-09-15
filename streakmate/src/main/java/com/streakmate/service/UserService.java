//package com.streakmate.service;
//
//import com.streakmate.dto.UserRegistrationDto;
//import com.streakmate.exception.DuplicateResourceException;
//import com.streakmate.exception.ResourceNotFoundException;
//import com.streakmate.model.User;
//import com.streakmate.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class UserService {
//
//    private final UserRepository userRepository;
//
//    @Transactional
//    public User register(UserRegistrationDto dto) {
//        if (userRepository.existsByEmail(dto.getEmail())) {
//            throw new DuplicateResourceException("Email already registered: " + dto.getEmail());
//        }
//        if (userRepository.existsByUsername(dto.getUsername())) {
//            throw new DuplicateResourceException("Username already taken: " + dto.getUsername());
//        }
//        User user = User.builder()
//                .name(dto.getName())
//                .email(dto.getEmail())
//                .username(dto.getUsername().toLowerCase())
//                .build();
//        User saved = userRepository.save(user);
//        log.info("New user registered: {} ({})", saved.getUsername(), saved.getEmail());
//        return saved;
//    }
//
//    public User findById(Long id) {
//        return userRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
//    }
//
//    public User findByUsername(String username) {
//        return userRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
//    }
//
//    public User findByEmail(String email) {
//        return userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
//    }
//
//    public List<User> findAll() {
//        return userRepository.findAll();
//    }
//
//    public boolean emailExists(String email) {
//        return userRepository.existsByEmail(email);
//    }
//
//    public boolean usernameExists(String username) {
//        return userRepository.existsByUsername(username);
//    }
//}

package com.streakmate.service;

import com.streakmate.dto.UserRegistrationDto;
import com.streakmate.exception.DuplicateResourceException;
import com.streakmate.exception.ResourceNotFoundException;
import com.streakmate.model.User;
import com.streakmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User register(UserRegistrationDto dto) {

        String email = dto.getEmail().trim().toLowerCase();
        String username = dto.getUsername().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException(
                    "Email already registered: " + email
            );
        }

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException(
                    "Username already taken: " + username
            );
        }

        User user = User.builder()
                .name(dto.getName().trim())
                .email(email)
                .username(username)
                .build();

        User saved = userRepository.save(user);

        log.info(
                "New user registered: {} ({})",
                saved.getUsername(),
                saved.getEmail()
        );

        return saved;
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(
                username.trim().toLowerCase()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "User not found: " + username
                )
        );
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(
                email.trim().toLowerCase()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "User not found with email: " + email
                )
        );
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(
                email.trim().toLowerCase()
        );
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(
                username.trim().toLowerCase()
        );
    }
}

