package com.carrental.service;

import com.carrental.exceptions.user.PasswordsDoesntMatchException;
import com.carrental.exceptions.user.UserAlreadyExistsException;
import com.carrental.models.User;
import com.carrental.models.mt.UserMT;
import com.carrental.models.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @AfterEach
    void clean() {
        userRepository.deleteAll();
    }


    @Test
    @DisplayName("Create user different passwords")
    void createUserPasswordsDifferent() {
        UserMT userMT = UserMT.builder()
                .username("test")
                .password("123456")
                .email("email@example.com")
                .build();
        Assertions.assertThrows(PasswordsDoesntMatchException.class, () -> userService.registerUser(userMT));
    }

    @Test
    @DisplayName("User created")
    void createUser() {
        final UserMT userMT = UserMT.builder()
                .username("test")
                .password("123456")
                .confirmPassword("123456")
                .email("email@example.com")
                .build();
        try {
            userService.registerUser(userMT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Optional<User> user = userRepository.findByUsername("test");

        assert user.isPresent();

    }

    @Test
    @DisplayName("User already exists")
    void createUserDuplicate() {
        final UserMT userMT = UserMT.builder()
                .username("test")
                .password("123456")
                .confirmPassword("123456")
                .email("email@example.com")
                .build();
        try {
            userService.registerUser(userMT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final UserMT userMT2 = UserMT.builder()
                .username("test")
                .password("123456")
                .confirmPassword("123456")
                .email("email@example.com")
                .build();

        Assertions.assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(userMT2));

    }


}