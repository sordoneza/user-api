package com.nisum.user.service;

import com.nisum.user.exception.ApiException;
import com.nisum.user.model.enums.Role;
import com.nisum.user.model.dto.PhoneDto;
import com.nisum.user.model.dto.UserInput;
import com.nisum.user.model.dto.UserLogin;
import com.nisum.user.model.dto.UserOutput;
import com.nisum.user.model.entity.Phone;
import com.nisum.user.model.entity.User;
import com.nisum.user.repository.UserRepo;
import com.nisum.user.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;

    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    @Value("${user.password.pattern}")
    private String passwordPattern;

    @Transactional
    @Override
    public UserOutput saveUser(UserInput userInput) {
        log.info("Saving new user {} to the database", userInput.getName());

        userRepo.findByEmail(userInput.getEmail()).ifPresent(user -> {
            throw new ApiException(HttpStatus.FORBIDDEN, "Email already registered");
        });

        if (!userInput.getPassword().matches(passwordPattern)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Invalid password pattern");
        }

        final User user = new User();
        user.setName(userInput.getName());
        user.setEmail(userInput.getEmail());
        user.setPassword(passwordEncoder.encode(userInput.getPassword()));
        user.setRole(Role.USER);

        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(user.getRole().name()));
        user.setToken(jwtUtil.generateToken(userInput.getEmail(), authorities));

        user.setActive(true);

        List<Phone> phones = new ArrayList<>();

        for (PhoneDto phoneDto : userInput.getPhones()) {
            Phone phone = new Phone();
            phone.setCityCode(phoneDto.getCityCode());
            phone.setCountryCode(phoneDto.getCountryCode());
            phone.setNumber(phoneDto.getNumber());
            phone.setUser(user);

            phones.add(phone);
        }

        user.setPhones(phones);

        User saveUser = userRepo.save(user);

        return UserOutput.toUserOutput(saveUser);
    }

    @Transactional
    @Override
    public String login(UserLogin userLogin) {
        User user = getUserByEmail(userLogin.getEmail());

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));

        String token = jwtUtil.generateToken(userLogin.getEmail(), Arrays.asList(new SimpleGrantedAuthority(user.getRole().name())));
        user.setToken(token);
        user.setActive(true);
        userRepo.save(user);

        return token;
    }

    @Override
    public User getUserById(String id) {
        User user = userRepo.findById(UUID.fromString(id)).<EntityNotFoundException>orElseThrow(() -> {
            throw new EntityNotFoundException("User not found");
        });

        return user;
    }

    @Override
    public User getUserByEmail(String email) throws EntityNotFoundException {
        log.info("Fetching user {}", email);

        User user = userRepo.findByEmail(email).<EntityNotFoundException>orElseThrow(() -> {
            throw new EntityNotFoundException("User not found");
        });

        return user;
    }

    @Override
    public List<User> getUsers() {
        log.info("Fetching all users");
        return userRepo.findAll();
    }


}
