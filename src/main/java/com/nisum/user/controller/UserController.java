package com.nisum.user.controller;

import com.nisum.user.model.dto.UserInput;
import com.nisum.user.model.dto.UserLogin;
import com.nisum.user.model.dto.UserOutput;
import com.nisum.user.model.entity.User;
import com.nisum.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get All Users")
    @SecurityRequirement(name = "JWT")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserOutput>> getUsers() {
        List<User> users = userService.getUsers();

        List<UserOutput> outputs = users.stream()
                .map(UserOutput::toUserOutput)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(outputs);
    }

    @Operation(summary = "Get User By Id")
    @SecurityRequirement(name = "JWT")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserOutput> getUser(@PathVariable String id) {
        UserOutput user = UserOutput.toUserOutput(userService.getUserById(id));
        return ResponseEntity.ok().body(user);
    }

    @Operation(summary = "Save new user")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserOutput> saveUser(@Valid @RequestBody UserInput user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(user));
    }

    @Operation(summary = "Login User")
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@Valid @RequestBody UserLogin userLogin) {
        String token = userService.login(userLogin);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        return ResponseEntity.ok().headers(headers).build();
    }

}
