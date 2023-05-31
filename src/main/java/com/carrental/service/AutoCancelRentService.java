package com.carrental.service;

import com.carrental.exceptions.rent.RentDoesNotExist;
import com.carrental.exceptions.user.UserDoesNotExistException;
import com.carrental.exceptions.user.UserIsNotAnOwnerException;
import com.carrental.models.Rent;
import com.carrental.models.User;
import com.carrental.models.enums.RentState;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Executors;


@Service
@RequiredArgsConstructor
@EnableScheduling
public class AutoCancelRentService {
    private static final Logger logger = LogManager.getLogger(AutoCancelRentService.class);

    private final EmailService emailService;

    private final RentService rentService;

    private final TemplateEngine templateEngine;

    public void cancelRent(Long id, Date date) {
        Date endDate = new Date(date.getTime() + DateUtils.MILLIS_PER_SECOND * 3);

        ScheduledTaskRegistrar scheduledTaskRegistrar = new ScheduledTaskRegistrar();
        scheduledTaskRegistrar.setScheduler(Executors.newScheduledThreadPool(1));

        scheduledTaskRegistrar.getScheduler().schedule(() -> {
            Optional<Rent> rentOptional = rentService.findById(id);

            if (rentOptional.isPresent()) {
                Rent rent = rentOptional.get();
                User user = rent.getUser();
                Long rentId = rent.getId();
                if (rent.getState().equals(RentState.PENDING)) {
                    try {
                        rentService.removeRent(rentId, user.getUsername());

                        //email
                        Context context = new Context();
                        context.setVariable("header", "Cancellation reservation");
                        context.setVariable("title", "Your reservation was cancelled after 3 days without confirmation!");
                        context.setVariable("rent", rent);
                        String body = templateEngine.process("email/rent-confirmation", context);

                        emailService.sendEmail(user.getEmail(), "Car reserved", body);

                        logger.log(Level.DEBUG, "Reservation was cancelled");
                    } catch (UserDoesNotExistException | RentDoesNotExist | UserIsNotAnOwnerException e) {
                        e.printStackTrace();
                        logger.error("{}", e.getMessage());
                    } catch (MessagingException e) {
                        logger.log(Level.ERROR, "sendEmail() - {}", e.getLocalizedMessage());
                    }
                }
            }
        }, endDate);
    }
}
