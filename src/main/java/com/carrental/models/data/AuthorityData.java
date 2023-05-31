package com.carrental.models.data;

import com.carrental.models.Authority;
import com.carrental.models.enums.AuthorityType;
import com.carrental.models.repository.AuthorityRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class AuthorityData {

    private final AuthorityRepository authorityRepository;

    @EventListener
    public void appReady(ApplicationReadyEvent event) {
        Optional<Authority> optionalAuthority = authorityRepository.findByName(AuthorityType.ROLE_USER);
        if (!optionalAuthority.isPresent())
            authorityRepository.save(Authority.builder().name(AuthorityType.ROLE_USER).build());
        optionalAuthority = authorityRepository.findByName(AuthorityType.ROLE_ADMIN);
        if (!optionalAuthority.isPresent())
            authorityRepository.save(Authority.builder().name(AuthorityType.ROLE_ADMIN).build());
        optionalAuthority = authorityRepository.findByName(AuthorityType.ROLE_OWNER);
        if (!optionalAuthority.isPresent())
            authorityRepository.save(Authority.builder().name(AuthorityType.ROLE_OWNER).build());
    }
}