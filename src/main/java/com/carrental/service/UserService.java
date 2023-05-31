package com.carrental.service;

import com.carrental.exceptions.user.NoRoleForUserException;
import com.carrental.exceptions.user.PasswordsDoesntMatchException;
import com.carrental.exceptions.user.UserAlreadyExistsException;
import com.carrental.mappers.UserMapper;
import com.carrental.models.Authority;
import com.carrental.models.Car;
import com.carrental.models.User;
import com.carrental.models.enums.AuthorityType;
import com.carrental.models.mt.UserMT;
import com.carrental.models.repository.AuthorityRepository;
import com.carrental.models.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final AuthorityRepository authorityRepository;

    private final UserMapper userMapper;

    public void registerUser(UserMT userMT) throws UserAlreadyExistsException, PasswordsDoesntMatchException, NoRoleForUserException {
        validatePasswordsMatch(userMT);
        Optional<User> userOptional = userRepository.findByUsername(userMT.getUsername());
        if (userOptional.isPresent())
            throw new UserAlreadyExistsException(String.format("User %s already exists!", userMT.getUsername()));
        User user = userMapper.convertUserMT(userMT);
        user.setEnabled(true);
        Optional<Authority> authorityOptional = authorityRepository.findByName(AuthorityType.ROLE_USER);
        Authority authority = authorityOptional.orElseThrow(() -> new NoRoleForUserException("No role in DB: ROLE_USER"));
        HashSet<Authority> authorities = new HashSet<>();
        authorities.add(authority);
        user.setAuthorities(authorities);
        userRepository.save(user);
    }

    public void registerAdmin(UserMT userMT) throws UserAlreadyExistsException, PasswordsDoesntMatchException, NoRoleForUserException {
        validatePasswordsMatch(userMT);
        Optional<User> userOptional = userRepository.findByUsername(userMT.getUsername());
        if (userOptional.isPresent())
            throw new UserAlreadyExistsException(String.format("User %s already exists!", userMT.getUsername()));
        User user = userMapper.convertUserMT(userMT);
        user.setEnabled(true);
        Optional<Authority> authorityOptional = authorityRepository.findByName(AuthorityType.ROLE_ADMIN);
        Authority authority = authorityOptional.orElseThrow(() -> new NoRoleForUserException("No role in DB: ROLE_USER"));
        HashSet<Authority> authorities = new HashSet<>();
        authorities.add(authority);
        user.setAuthorities(authorities);
        userRepository.save(user);
    }

    public Optional<User> findUserById(Integer id) {
        return userRepository.findUserById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAllByOrderByIdAsc() {
        return userRepository.findAllByOrderByIdAsc();
    }

    private void validatePasswordsMatch(UserMT userMT) throws PasswordsDoesntMatchException {

        if (StringUtils.compare(userMT.getPassword(), userMT.getConfirmPassword()) != 0) {
            throw new PasswordsDoesntMatchException("Passwords aren't the same!");
        }
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }
}
