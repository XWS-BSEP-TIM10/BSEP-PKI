package com.Bsep.controller;

import com.Bsep.dto.Change2FAStatusDTO;
import com.Bsep.dto.ChangePasswordDTO;
import com.Bsep.dto.TwoFactorAuthenticationDTO;
import com.Bsep.exception.UserNotFoundException;
import com.Bsep.exception.WrongPasswordException;
import com.Bsep.model.User;
import com.Bsep.service.LoggerService;
import com.Bsep.service.UserService;
import com.Bsep.service.impl.LoggerServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @PutMapping("/change-password")
    @PreAuthorize("hasAuthority('CHANGE_PASSWORD_PERMISSION')")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordDTO changePasswordDTO) {
        try {
            User changedUser = userService.changePassword(changePasswordDTO, SecurityContextHolder.getContext().getAuthentication().getName());
            if(changedUser == null) {
                loggerService.passwordChangingFailed("Saving new password failed", SecurityContextHolder.getContext().getAuthentication().getName());
                return ResponseEntity.internalServerError().build();
            }
            loggerService.passwordChanged(SecurityContextHolder.getContext().getAuthentication().getName());
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            loggerService.passwordChangingFailed(e.getMessage(), SecurityContextHolder.getContext().getAuthentication().getName());
            return ResponseEntity.notFound().build();
        } catch (WrongPasswordException e) {
            loggerService.passwordChangingFailed(e.getMessage(), SecurityContextHolder.getContext().getAuthentication().getName());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/2fa-status")
    @PreAuthorize("hasAuthority('GET_2FA_STATUS_PERMISSION')")
    public ResponseEntity<?> get2FAStatus() {
       String username = SecurityContextHolder.getContext().getAuthentication().getName();
       User user = userService.findByUsername(username);
       loggerService.get2FAStatus(username);
       return new ResponseEntity<>(new TwoFactorAuthenticationDTO(user.isUsing2FA(),user.getSecret()), HttpStatus.OK);
    }
    
    @PutMapping("/2fa-status")
    @PreAuthorize("hasAuthority('CHANGE_2FA_STATUS_PERMISSION')")
    public ResponseEntity<?> change2FAStatus(@RequestBody Change2FAStatusDTO change2faStatusDTO) {
    	String username = "";
        try {
        	username = SecurityContextHolder.getContext().getAuthentication().getName();
            userService.change2FAStatus(username, change2faStatusDTO.getIsEnabled());
            loggerService.change2FAStatus(username, change2faStatusDTO.getIsEnabled());
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
        	loggerService.change2FAStatusFailed(username);
            return ResponseEntity.notFound().build();
        }
    }
    
    

}
