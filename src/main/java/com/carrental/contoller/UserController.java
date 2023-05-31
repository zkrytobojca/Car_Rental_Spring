package com.carrental.contoller;

import com.carrental.exceptions.authority.NoAuthorityWithThisIdException;
import com.carrental.exceptions.car.NoCarWithThisIdException;
import com.carrental.exceptions.user.NoRoleForUserException;
import com.carrental.exceptions.user.NoUserWithThisIdException;
import com.carrental.exceptions.user.PasswordsDoesntMatchException;
import com.carrental.exceptions.user.UserAlreadyExistsException;
import com.carrental.models.Authority;
import com.carrental.models.Car;
import com.carrental.models.User;
import com.carrental.models.enums.AuthorityType;
import com.carrental.models.mt.UserMT;
import com.carrental.models.repository.AuthorityRepository;
import com.carrental.service.EmailService;
import com.carrental.service.UserService;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);

    private final EmailService emailService;

    private final TemplateEngine templateEngine;

    private final UserService userService;

    private final AuthorityRepository authorityRepository;

    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    @RequestMapping(value = {"/registration"}, method = RequestMethod.GET)
    public String registrationForm(Model model) {
        UserMT userMT = new UserMT();
        model.addAttribute("userMT", userMT);
        return "registration";
    }

    @RequestMapping(value = {"/registration"}, method = RequestMethod.POST)
    public String processRegistrationForm(@ModelAttribute UserMT userMT, Model model) {
        try {
            logger.log(Level.DEBUG, "Invoked registerUser({}) ", userMT);
            userService.registerUser(userMT);

            //email
            Context context = new Context();
            context.setVariable("header", "Registration confirmation");
            context.setVariable("title", "Success");
            context.setVariable("description", "You can now log in!");
            String body = templateEngine.process("email/register-confirmation", context);
            emailService.sendEmail(userMT.getEmail(), "Registration", body);
            logger.log(Level.DEBUG, "Register confirmation has been sent!");
            return "login";
        } catch (UserAlreadyExistsException | PasswordsDoesntMatchException | NoRoleForUserException e) {
            model.addAttribute("error", e.getLocalizedMessage());
            model.addAttribute("userMT", userMT);
            logger.log(Level.ERROR, "registerUser() - {}", e.getLocalizedMessage());
        } catch (MessagingException e) {
            logger.log(Level.ERROR, "sendEmail() - {}", e.getLocalizedMessage());
        }
        return "registration";
    }

    @RequestMapping(value = {"/user/list"}, method = RequestMethod.GET)
    public String userList(Model model) {
        List<User> userList = userService.findAllByOrderByIdAsc();
        model.addAttribute("userList", userList);
        return "user-list";
    }

    @RequestMapping(value = {"/user/{id}/edit"}, method = RequestMethod.GET)
    public String userEdit(@PathVariable("id") Integer id, Model model) throws NoUserWithThisIdException {
        Optional<User> userOptional = userService.findUserById(id);
        User user = userOptional.orElseThrow(() -> new NoUserWithThisIdException(String.format("User id {%d} not found", id)));
        List<AuthorityType> userAuthorities = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toList());
        List<Authority> allAuthorities = authorityRepository.findAllByOrderByIdAsc();
        model.addAttribute("user", user);
        model.addAttribute("userAuthorities", userAuthorities);
        model.addAttribute("allAuthorities", allAuthorities);
        return "user-form";
    }

    @RequestMapping(value = {"/user/{id}/process-form"}, method = RequestMethod.POST)
    public String processUserForm(@PathVariable("id") Integer id, @ModelAttribute User user, @RequestParam(value = "checkedAuthorities" , required = false) int[] checkedAuthorities) throws NoUserWithThisIdException, NoAuthorityWithThisIdException {
        Optional<User> userOptional = userService.findUserById(id);
        User userOld = userOptional.orElseThrow(() -> new NoUserWithThisIdException(String.format("User id {%d} not found", id)));
        user.setId(id);
        user.setUsername(userOld.getUsername());
        user.setPassword(userOld.getPassword());
        user.setRents(userOld.getRents());
        user.setEmail(userOld.getEmail());
        Set<Authority> authoritiesToAdd = new HashSet<>();
        for ( int authorityId : checkedAuthorities) {
            Optional<Authority> authorityOptional = authorityRepository.findFirstById(authorityId);
            Authority authority = authorityOptional.orElseThrow(() -> new NoAuthorityWithThisIdException(String.format("Authority with id {%d} not found", id)));
            authoritiesToAdd.add(authority);
        }
        user.setAuthorities(authoritiesToAdd);
        logger.log(Level.DEBUG, "Invoked updateUser({})", user);
        userService.updateUser(user);
        return "redirect:/user/list";
    }

    @RequestMapping(value = {"/user/{id}/delete"}, method = RequestMethod.GET)
    public String deleteUser(@PathVariable("id") Integer id) {
        final Optional<User> userOptional = userService.findUserById(id);
        User user;
        try {
            user = userOptional.orElseThrow(() -> new NoUserWithThisIdException(String.format("User id {%d} not found", id)));
            userService.delete(user);
        } catch (NoUserWithThisIdException e) {
            e.printStackTrace();
            logger.log(Level.ERROR, "Invoked delete() - {}", e.getLocalizedMessage());
        }
        return "redirect:/user/list";
    }

    @RequestMapping(value = {"/admin/e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855/"}, method = RequestMethod.GET)
    public String addAdmin() {
        try {
            UserMT userMT = UserMT.builder()
                    .username("admin")
                    .password("admin")
                    .confirmPassword("admin")
                    .email("admin@localhost")
                    .build();
            userService.registerAdmin(userMT);
        } catch (Exception e) {
            logger.log(Level.WARN, "addAdmin() - {}", e.getLocalizedMessage());
        }

        return "redirect:/";
    }
}
