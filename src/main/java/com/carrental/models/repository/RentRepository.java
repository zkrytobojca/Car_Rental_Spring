package com.carrental.models.repository;

import com.carrental.models.Rent;
import com.carrental.models.User;
import com.carrental.models.enums.RentState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RentRepository extends JpaRepository<Rent, Long> {
    List<Rent> getAllByUserAndState(User user, RentState state);
}
