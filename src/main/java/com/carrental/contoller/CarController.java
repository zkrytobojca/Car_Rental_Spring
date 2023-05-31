package com.carrental.contoller;

import com.carrental.exceptions.car.NoCarWithThisIdException;
import com.carrental.models.Car;
import com.carrental.models.Rent;
import com.carrental.service.CarService;
import com.carrental.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/car")
public class CarController {
    private static final Logger logger = LogManager.getLogger(CarController.class);

    private final CarService carService;

    @RequestMapping(value = {"/add"}, method = RequestMethod.GET)
    public String carForm(Model model) {
        Car car = new Car();
        model.addAttribute("car", car);
        return "car-form";
    }

    @RequestMapping(value = {"/{id}/process-form"}, method = RequestMethod.POST)
    public String processCarForm(@PathVariable("id") long id, @ModelAttribute Car car, @RequestParam("file") MultipartFile file) {
        car.setId(id);
        if (StringUtils.isBlank(file.getOriginalFilename())) {
            Optional<Car> carOptional = carService.findCarById(id);
            carOptional.ifPresent(carOpt -> car.setImage(carOpt.getImage()));
        } else {
            car.setImage(ImageUtils.toBytes(file));
        }
        logger.log(Level.DEBUG, "Invoked updateOrCreateCar({})", car);
        carService.updateOrCreateCar(car);
        return "redirect:/car/list";
    }

    @RequestMapping(value = {"/list"}, method = RequestMethod.GET)
    public String carList(Model model) {
        List<Car> carList = carService.findAllByOrderByIdAsc();
        model.addAttribute("carList", carList);
        return "car-list";
    }

    @RequestMapping(value = {"/{id}/edit"}, method = RequestMethod.GET)
    public String editCar(@PathVariable("id") Long id, Model model) throws NoCarWithThisIdException {
        Optional<Car> carOptional = carService.findCarById(id);
        Car car = carOptional.orElseThrow(() -> new NoCarWithThisIdException(String.format("Car id [%d} not found", id)));
        model.addAttribute("car", car);
        return "car-form";
    }

    @RequestMapping(value = {"/{id}/details"}, method = RequestMethod.GET)
    public String carDetails(@PathVariable("id") Long id, Model model) {
        Optional<Car> carOptional = carService.findCarById(id);
        Car car = null;
        try {
            car = carOptional.orElseThrow(() -> new NoCarWithThisIdException(String.format("Car id [%d} not found", id)));
        } catch (NoCarWithThisIdException e) {
            logger.log(Level.ERROR, "Invoked updateOrCreateCar() - {}", e.getLocalizedMessage());
        }
        model.addAttribute("car", car);
        Rent rent = car.getRent();
        model.addAttribute("rent", rent);
        return "car-details";
    }

    @RequestMapping(value = {"/{id}/delete"}, method = RequestMethod.GET)
    public String deleteCar(@PathVariable("id") Long id) {
        final Optional<Car> carOptional = carService.findCarById(id);
        Car car;
        try {
            car = carOptional.orElseThrow(() -> new NoCarWithThisIdException(String.format("Car id {%d} not found", id)));
            carService.delete(car);
        } catch (NoCarWithThisIdException e) {
            e.printStackTrace();
            logger.log(Level.ERROR, "Invoked delete() - {}", e.getLocalizedMessage());
        }
        return "redirect:/car/list";
    }

    @RequestMapping(value = {"/{id}/rent"}, method = RequestMethod.GET)
    public String rentCar(@PathVariable("id") long id, Model model) throws NoCarWithThisIdException {
        Optional<Car> carOptional = carService.findCarById(id);
        Car car = carOptional.orElseThrow(() -> new NoCarWithThisIdException(String.format("Car id [%d} not found", id)));

        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 1);
        dt = c.getTime();

        model.addAttribute("car", car);
        model.addAttribute("dateFrom", new SimpleDateFormat("yyyy-MM-dd HH:MM:ss").format(dt));
        model.addAttribute("rentLength", 1);
        return "rent-car";
    }
}
