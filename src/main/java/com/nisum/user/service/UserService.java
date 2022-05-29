package com.nisum.user.service;

import com.nisum.user.model.dto.UserInput;
import com.nisum.user.model.dto.UserLogin;
import com.nisum.user.model.dto.UserOutput;
import com.nisum.user.model.entity.User;

import java.util.List;

public interface UserService {
    UserOutput saveUser(UserInput user);
    String login(UserLogin userLogin);
    User getUserById(String id);
    User getUserByEmail(String username);
    List<User> getUsers();
}
