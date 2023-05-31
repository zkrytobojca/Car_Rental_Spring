package com.carrental.service;

import com.carrental.models.Car;
import com.carrental.models.repository.CarRepository;
import com.carrental.models.repository.RentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    public void updateOrCreateCar(Car car) {
        carRepository.save(car);
    }

    public Optional<Car> findCarById(Long id) {
        return carRepository.findById(id);
    }

    public List<Car> findAllByOrderByIdAsc() {
        return carRepository.findAllByOrderByIdAsc();
    }

    public void delete(Car car) {
        carRepository.delete(car);
    }

    public void saveCar(Car car) {
        carRepository.save(car);
    }
}
