package com.carrental.service;

import com.carrental.models.Car;
import com.carrental.models.repository.CarRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CarServiceTest {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarService carService;

    @AfterEach
    void prepare() {
        carRepository.deleteAll();
    }


    @Test
    @DisplayName("Creating a car")
    void testCarCreate() {
        Car car = Car.builder()
                .available(true)
                .brand("Tesla")
                .model("Roadster")
                .mileage(20)
                .productionYear(2020)
                .rentPrice(new BigDecimal(2000))
                .build();

        carService.updateOrCreateCar(car);
        final List<Car> all = carRepository.findAll();
        assert all.size() == 1;
        Car c = all.get(0);
        assertThat(car.getBrand()).isEqualTo(c.getBrand());
        assertThat(car.getModel()).isEqualTo(c.getModel());
        assertThat(car.getMileage()).isEqualTo(c.getMileage());
        assertThat(car.getProductionYear()).isEqualTo(c.getProductionYear());
        assertThat(car.getRentPrice()).isEqualByComparingTo(c.getRentPrice());
    }

    @Test
    @DisplayName("Editing car")
    void testCarEdit() {
        Car car = Car.builder()
                .available(true)
                .brand("Tesla")
                .model("Roadster")
                .mileage(20)
                .productionYear(2020)
                .rentPrice(new BigDecimal(2000))
                .build();

        carService.updateOrCreateCar(car);
        car.setBrand("Skoda");
        car.setModel("Octavia");
        car.setMileage(99999);
        carService.updateOrCreateCar(car);
        final List<Car> all = carRepository.findAll();
        Car c = all.get(0);
        assertThat(car.getBrand()).isEqualTo(c.getBrand());
        assertThat(car.getModel()).isEqualTo(c.getModel());
        assertThat(car.getMileage()).isEqualTo(c.getMileage());
        assertThat(car.getProductionYear()).isEqualTo(c.getProductionYear());
        assertThat(car.getRentPrice()).isEqualByComparingTo(c.getRentPrice());
    }

    @Test
    @DisplayName("Deleting car")
    void testCarDelete() {
        Car car = Car.builder()
                .available(true)
                .brand("Tesla")
                .model("Roadster")
                .mileage(20)
                .productionYear(2020)
                .rentPrice(new BigDecimal(2000))
                .build();

        carService.updateOrCreateCar(car);
        carRepository.delete(car);
        final List<Car> all = carRepository.findAll();
        assertThat(all.isEmpty());
    }
}