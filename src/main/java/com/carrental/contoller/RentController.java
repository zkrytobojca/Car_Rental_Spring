package com.carrental.contoller;

import com.carrental.exceptions.rent.RentDoesNotExist;
import com.carrental.exceptions.user.UserDoesNotExistException;
import com.carrental.exceptions.user.UserIsNotAnOwnerException;
import com.carrental.models.Car;
import com.carrental.models.Rent;
import com.carrental.models.User;
import com.carrental.models.enums.OperationStatus;
import com.carrental.models.enums.RentState;
import com.carrental.models.mt.OperationDetails;
import com.carrental.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/rent")
@SessionAttributes("details")
public class RentController {
    private static final Logger logger = LogManager.getLogger(RentController.class);

    private final RentService rentService;

    private final CarService carService;

    private final UserService userService;

    private final EmailService emailService;

    private final TemplateEngine templateEngine;

    private final AutoCancelRentService autoCancelRentService;
    
    @ModelAttribute("details")
    public OperationDetails details() {
        return new OperationDetails();
    }

    @RequestMapping(value = {"/{id}/proceed"}, method = RequestMethod.POST)
    public String proceed(@PathVariable("id") long id,
                          @ModelAttribute(value = "dateFrom") String dateFrom,
                          @ModelAttribute(value = "rentLength") String rentLength) {
        final String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateFrom);
        } catch (ParseException e) {
            logger.log(Level.ERROR, "proceed() - {}", e.getLocalizedMessage());
        }
        int length = Integer.parseInt(rentLength);
        final Optional<User> userOptional = userService.findByUsername(currentUserName);
        try {
            userOptional.orElseThrow(() -> new UserDoesNotExistException(String.format("User %s does not exist!", currentUserName)));
        } catch (UserDoesNotExistException e) {
            logger.log(Level.ERROR, "userOptional - {}", e.getLocalizedMessage());
        }

        Rent rent = new Rent();
        Optional<Car> carOptional = carService.findCarById(id);
        userOptional.ifPresent(rent::setUser);
        carOptional.ifPresent(car -> {
            rent.setCar(car);
            rent.setPayment(car.getRentPrice().multiply(BigDecimal.valueOf(length)));
            car.setAvailable(!car.isAvailable());
            carService.saveCar(car);
        });
        rent.setRentDate(date);
        rent.setRentLength(length);
        rent.setState(RentState.PENDING);
        rentService.save(rent);

        //set timer
        autoCancelRentService.cancelRent(rent.getId(), rent.getRentDate());

        //email
        Context context = new Context();
        context.setVariable("header", "Rent reservation");
        context.setVariable("title", "You have reserved a car!");
        context.setVariable("rent", rent);
        String body = templateEngine.process("email/rent-confirmation", context);
        if (userOptional.isPresent()) {
            try {
                emailService.sendEmail(Objects.requireNonNull(userOptional.get()).getEmail(), "Car reserved", body);
            } catch (MessagingException e) {
                logger.log(Level.ERROR, "sendEmail() - {}", e.getLocalizedMessage());
            }
        }
        logger.log(Level.DEBUG, "Rent reservation has been sent!");

        return "redirect:/rent/list";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String listRents(Model model, @ModelAttribute("details") OperationDetails operationDetails, SessionStatus sessionStatus) {
        final String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            prepareRents(model, username);
            model.addAttribute("operationDetails", operationDetails);
            sessionStatus.setComplete();
            return "rent-list";
        } catch (UserDoesNotExistException e) {
            e.printStackTrace();
            logger.log(Level.ERROR, e.getLocalizedMessage());
        }
        return "index";
    }

    @RequestMapping(value = "/{id}/remove", method = RequestMethod.GET)
    public String removeRent(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, @ModelAttribute("details") OperationDetails operationDetails) {
        final String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {

            final Optional<User> userOptional = userService.findByUsername(username);
            final Optional<Rent> rentOptional = rentService.findById(id);
            Rent rent = null;
            if (rentOptional.isPresent())
                rent = rentOptional.get();
            User user = null;
            if (userOptional.isPresent()) {
                user = userOptional.get();
            }
            //email
            Context context = new Context();
            context.setVariable("header", "Rent cancellation");
            context.setVariable("title", "You have cancelled your reservation!");
            context.setVariable("rent", rent);
            String body = templateEngine.process("email/rent-confirmation", context);
            rentService.removeRent(id, username);
            operationDetails.setOperationStatus(OperationStatus.CANCELLED);
            emailService.sendEmail(Objects.requireNonNull(user).getEmail(), "Rent cancelled", body);
            logger.log(Level.DEBUG, "Rent cancellation has been sent!");

        } catch (UserDoesNotExistException | UserIsNotAnOwnerException | RentDoesNotExist e) {
            operationDetails.setOperationStatus(OperationStatus.ERROR);
            operationDetails.setMessage(e.getLocalizedMessage());
            logger.log(Level.ERROR, "removeRent() - {}", e.getLocalizedMessage());
        } catch (MessagingException e) {
            logger.log(Level.ERROR, "sendEmail() - {}", e.getLocalizedMessage());
        }
        redirectAttributes.addFlashAttribute("details", operationDetails);
        return "redirect:/rent/list";
    }

    @RequestMapping(value = "/{id}/confirm", method = RequestMethod.GET)
    public String confirmRent(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, @ModelAttribute("details") OperationDetails operationDetails) {
        final String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            rentService.confirmRent(id, username);
            operationDetails.setOperationStatus(OperationStatus.SUCCESS);

            final Optional<User> userOptional = userService.findByUsername(username);
            final Optional<Rent> rentOptional = rentService.findById(id);
            Rent rent = null;
            if (rentOptional.isPresent())
                rent = rentOptional.get();
            User user = null;
            if (userOptional.isPresent()) {
                user = userOptional.get();
            }
            //email
            Context context = new Context();
            context.setVariable("header", "Rent confirmation");
            context.setVariable("title", "You have rented a car!");
            context.setVariable("rent", rent);
            String body = templateEngine.process("email/rent-confirmation", context);
            emailService.sendEmail(Objects.requireNonNull(user).getEmail(), "Rent confirmation", body);
            logger.log(Level.DEBUG, "Rent confirmation has been sent!");

        } catch (RentDoesNotExist | UserDoesNotExistException | UserIsNotAnOwnerException e) {
            operationDetails.setOperationStatus(OperationStatus.ERROR);
            operationDetails.setMessage(e.getLocalizedMessage());
            logger.log(Level.ERROR, "confirmRent() - {}", e.getLocalizedMessage());
        } catch (MessagingException e) {
            logger.log(Level.ERROR, "sendEmail() - {}", e.getLocalizedMessage());
        }
        redirectAttributes.addFlashAttribute("details", operationDetails);
        return "redirect:/rent/list";
    }

    private void prepareRents(Model model, String username) throws UserDoesNotExistException {
        final List<Rent> pending = rentService.getAllRentsByUsernameAndState(username, RentState.PENDING);
        final List<Rent> confirmed = rentService.getAllRentsByUsernameAndState(username, RentState.CONFIRMED);
        final List<Rent> finished = rentService.getAllRentsByUsernameAndState(username, RentState.FINISHED);
        model.addAttribute("pending", pending);
        model.addAttribute("confirmed", confirmed);
        model.addAttribute("finished", finished);
    }
}
