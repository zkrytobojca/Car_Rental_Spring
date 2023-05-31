package com.carrental.models.data;

import com.carrental.models.Authority;
import com.carrental.models.enums.AuthorityType;
import com.carrental.models.repository.AuthorityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class AuthorityDataTest {

    @Autowired
    private AuthorityRepository authorityRepository;


    @Test
    @DisplayName("ROLE_USER exists")
    void test() {
        final Optional<Authority> authority = authorityRepository.findByName(AuthorityType.ROLE_USER);
        assert authority.isPresent();
    }

    @Test
    @DisplayName("ROLE_OWNER exists")
    void test2() {
        final Optional<Authority> authority = authorityRepository.findByName(AuthorityType.ROLE_OWNER);
        assert authority.isPresent();
    }

    @Test
    @DisplayName("ROLE_ADMIN exists")
    void test3() {
        final Optional<Authority> authority = authorityRepository.findByName(AuthorityType.ROLE_ADMIN);
        assert authority.isPresent();
    }

}