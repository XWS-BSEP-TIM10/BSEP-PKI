package com.Bsep.controller;

import com.Bsep.service.LoggerService;
import com.Bsep.service.UserService;
import com.Bsep.service.impl.LoggerServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserController {

    private UserService userService;
    private final LoggerService loggerService;

    public UserController(UserService userService) {
        this.userService = userService;
        this.loggerService = new LoggerServiceImpl(this.getClass());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllUsers")
    @PreAuthorize("hasAuthority('GET_USERS_PERMISSION')")
    public ResponseEntity<?> getAllUsers() {
        loggerService.getAllUsers(SecurityContextHolder.getContext().getAuthentication().getName());
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

}
