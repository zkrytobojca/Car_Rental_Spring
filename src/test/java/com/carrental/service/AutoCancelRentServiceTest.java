package com.carrental.service;

import com.carrental.exceptions.rent.RentDoesNotExist;
import com.carrental.exceptions.user.UserDoesNotExistException;
import com.carrental.exceptions.user.UserIsNotAnOwnerException;
import com.carrental.models.Car;
import com.carrental.models.Rent;
import com.carrental.models.User;
import com.carrental.models.enums.RentState;
import com.carrental.models.mt.UserMT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@SpringBootTest
public class AutoCancelRentServiceTest {
    @Autowired
    private AutoCancelRentService autoCancelRentService;

    @Autowired
    private RentService rentService;

    @Autowired
    private CarService carService;

    @Autowired
    private UserService userService;

    private Car car;
    private User user;
    private Rent actual;

    // before executing tests change from days to seconds in cancelRent() method
    @BeforeEach
    void prepare() {
        //create User
        final UserMT userMT = UserMT.builder()
                .username("xxxxtest")
                .password("123456")
                .confirmPassword("123456")
                .email("email@example.com")
                .build();
        try {
            userService.registerUser(userMT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        user = userService.findByUsername(userMT.getUsername()).get();

        //create Car
        car = Car.builder()
                .available(true)
                .brand("Tesla")
                .model("Roadster")
                .mileage(20)
                .productionYear(2020)
                .rentPrice(new BigDecimal(2000).setScale(2))
                .build();

        carService.updateOrCreateCar(car);
    }

    @AfterEach
    void clear() {
        rentService.delete(actual);
        carService.delete(car);
        userService.delete(user);
    }


    @Test
    @DisplayName("Automatic rent cancel after some time without confirmation")
    void testAutoCancelRentWithoutConfirmation() {
        int rentLength = 10;
        BigDecimal payment = car.getRentPrice().multiply(BigDecimal.valueOf(rentLength)).setScale(2);
        actual = Rent.builder()
                .user(user)
                .car(car)
                .rentDate(new Date())
                .payment(payment)
                .rentLength(rentLength)
                .state(RentState.PENDING)
                .build();

        Rent saved = rentService.save(actual);
        actual.setId(saved.getId());

        autoCancelRentService.cancelRent(saved.getId(), saved.getRentDate());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Optional<Rent> expectedOptional = rentService.findById(actual.getId());

        Optional<Car> expectedCarOptional = carService.findCarById(actual.getCar().getId());

        Car expectedCar = null;
        if (expectedCarOptional.isPresent()) {
            expectedCar = expectedCarOptional.get();
        }

        assert !expectedOptional.isPresent();
        assert expectedCar != null;
        assert expectedCar.isAvailable();
    }

    @Test
    @DisplayName("Automatic rent cancel after some time with confirmation")
    void testAutoCancelRentWithConfirmation() {
        int rentLength = 10;
        BigDecimal payment = car.getRentPrice().multiply(BigDecimal.valueOf(rentLength)).setScale(2);
        actual = Rent.builder()
                .user(user)
                .car(car)
                .rentDate(new Date())
                .payment(payment)
                .rentLength(rentLength)
                .state(RentState.PENDING)
                .build();

        Rent saved = rentService.save(actual);
        actual.setId(saved.getId());

        autoCancelRentService.cancelRent(saved.getId(), saved.getRentDate());

        try {
            rentService.confirmRent(saved.getId(), saved.getUser().getUsername());
        } catch (RentDoesNotExist | UserDoesNotExistException | UserIsNotAnOwnerException e ) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Optional<Rent> expectedOptional = rentService.findById(actual.getId());
        Rent expected = expectedOptional.get();

        assert expectedOptional.isPresent();
        assert expected.getState().equals(RentState.CONFIRMED);
    }

}
