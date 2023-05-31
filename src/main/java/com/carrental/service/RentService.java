package com.carrental.service;

import com.carrental.aspects.CustomLogger;
import com.carrental.aspects.LogFinest;
import com.carrental.aspects.LogIgnore;
import com.carrental.exceptions.rent.RentDoesNotExist;
import com.carrental.exceptions.user.UserDoesNotExistException;
import com.carrental.exceptions.user.UserIsNotAnOwnerException;
import com.carrental.models.Car;
import com.carrental.models.Rent;
import com.carrental.models.User;
import com.carrental.models.enums.RentState;
import com.carrental.models.repository.CarRepository;
import com.carrental.models.repository.RentRepository;
import com.carrental.models.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CustomLogger
@RequiredArgsConstructor
public class RentService {
    private final RentRepository rentRepository;

    private final UserRepository userRepository;

    private final CarRepository carRepository;

    public Rent save(Rent rent) {
        return rentRepository.save(rent);
    }

    @LogIgnore
    public List<Rent> getAllRentsByUsernameAndState(String username, RentState state) throws UserDoesNotExistException {
        final Optional<User> user = userRepository.findByUsername(username);
        user.orElseThrow(() -> new UserDoesNotExistException(String.format("User %s doesn't exist!", username)));
        return rentRepository.getAllByUserAndState(user.get(), state);
    }

    @LogFinest
    public void removeRent(Long rentId, String username) throws UserDoesNotExistException, RentDoesNotExist, UserIsNotAnOwnerException {
        final Rent rent = prepare(rentId, username);

        Car car = rent.getCar();
        rentRepository.delete(rent);
        car.setAvailable(true);
        carRepository.save(car);
    }

    @LogFinest
    public void confirmRent(Long id, String username) throws RentDoesNotExist, UserDoesNotExistException, UserIsNotAnOwnerException {
        final Rent rent = prepare(id, username);

        final Optional<User> optionalUser = userRepository.findByUsername(username);
        User user = null;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        }

        rent.setState(RentState.CONFIRMED);
        rent.setUser(user);
        rentRepository.save(rent);
    }

    @LogFinest
    private Rent prepare(Long id, String username) throws RentDoesNotExist, UserDoesNotExistException, UserIsNotAnOwnerException {
        final Optional<User> userOptional = userRepository.findByUsername(username);
        final Optional<Rent> rentOptional = rentRepository.findById(id);
        rentOptional.orElseThrow(() -> new RentDoesNotExist(String.format("Rent with id=%s does not exist", id)));
        userOptional.orElseThrow(() -> new UserDoesNotExistException(String.format("User %s does not exist", username)));

        final Rent rent = rentOptional.get();
        final User user = userOptional.get();

        if (!rent.getUser().equals(user))
            throw new UserIsNotAnOwnerException(String.format("User %s is not an owner or rent with id %s", user.getId(), rent.getId()));
        return rent;
    }

    public void delete(Rent rent) {
        rentRepository.delete(rent);
    }

    public Optional<Rent> findById(long id) {
        return rentRepository.findById(id);
    }
}
