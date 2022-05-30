package com.nisum.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nisum.user.TestSecurityConfig;
import com.nisum.user.exception.ApiException;
import com.nisum.user.model.dto.PhoneDto;
import com.nisum.user.model.dto.UserInput;
import com.nisum.user.model.dto.UserOutput;
import com.nisum.user.repository.UserRepo;
import com.nisum.user.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestSecurityConfig.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserInput userInput;

    private UserOutput userOutput;

    private static ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        userInput = getUserInput();
        userOutput = getUserOutput();
    }

    @Test
    void whenInputValidUser_itShouldSucceed() throws Exception {
        when(userService.saveUser(userInput)).thenReturn(userOutput);

        String json = mapper.writeValueAsString(userInput);

        mockMvc.perform(post("/api/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }


    @Test
    void whenPasswordMissed_itShouldFail() throws Exception {
        String json = mapper.writeValueAsString(getInvalidInput());

        mockMvc.perform(post("/api/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.containsString("password")));
    }

    @Test
    void whenInvalidEmail_itShouldFail() throws Exception {
        String json = mapper.writeValueAsString(getInvalidEmailInput());

        mockMvc.perform(post("/api/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.containsString("email")));
    }

    @Test
    void whenPasswordDoesNotMetPattern_itShouldThrowError() throws Exception {
        String message = "Invalid password pattern";

        when(userService.saveUser(userInput))
                .thenThrow(new ApiException(HttpStatus.FORBIDDEN, message));

        String json = mapper.writeValueAsString(userInput);

        mockMvc.perform(post("/api/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message",  Matchers.containsString(message)));
    }

    private UserInput getUserInput() {
        UserInput input = new UserInput();
        input.setName("John");
        input.setEmail("jonhdoe@mail.com");
        input.setPassword("Random1$");

        PhoneDto phoneDto = new PhoneDto();
        phoneDto.setCityCode("CH");
        phoneDto.setCountryCode("US");
        phoneDto.setNumber("124545344");

        input.setPhones(Arrays.asList(phoneDto));
        return input;
    }

    private UserInput getInvalidInput() {
        UserInput input = new UserInput();
        input.setName("John");
        input.setEmail("mail@mail.com");

        return input;
    }

    private UserInput getInvalidEmailInput() {
        UserInput input = new UserInput();
        input.setName("John");
        input.setEmail("asdfaasdfadf");
        input.setPassword("asdfasdfadsf");

        return input;
    }

    private UserOutput getUserOutput() {
        Date now = new Date();
        UserOutput userOutput = new UserOutput();
        userOutput.setId(UUID.randomUUID());
        userOutput.setName(userInput.getName());
        userOutput.setEmail(userInput.getEmail());
        userOutput.setActive(true);
        userOutput.setCreated(now);
        userOutput.setLastLogin(now);
        userOutput.setModified(now);
        userOutput.setToken("asdfasdfasdfasdf54a5sd4ffa6sdf5as4df54assd5f");

        return userOutput;
    }
}
