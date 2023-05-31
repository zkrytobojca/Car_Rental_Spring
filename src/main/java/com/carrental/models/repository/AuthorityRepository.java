package com.carrental.models.repository;

import com.carrental.models.Authority;
import com.carrental.models.Car;
import com.carrental.models.enums.AuthorityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Optional<Authority> findByName(AuthorityType type);

    Optional<Authority> findFirstById(Integer id);

    List<Authority> findAllByOrderByIdAsc();
}
