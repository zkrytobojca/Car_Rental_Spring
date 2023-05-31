package com.carrental.models.repository;

import com.carrental.models.Car;
import com.carrental.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findUserById(Integer id);

    List<User> findAllByOrderByIdAsc();
}
