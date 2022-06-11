package com.Bsep.controller;

import com.Bsep.dto.LoginDTO;
import com.Bsep.dto.TokenDTO;
import com.Bsep.service.AuthenticationService;
import com.Bsep.service.LoggerService;
import com.Bsep.service.impl.LoggerServiceImpl;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/auth")
public class AuthenticationController {

    private final LoggerService loggerService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        this.loggerService = new LoggerServiceImpl(this.getClass());
    }

    @PostMapping(value = "/login")
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid LoginDTO loginDTO) {
        TokenDTO tokenDTO = authenticationService.login(loginDTO.getUsername(), loginDTO.getPassword());
        if(tokenDTO != null) {
            loggerService.loginSuccess(loginDTO.getUsername());
            return ResponseEntity.ok(tokenDTO);
        }
        loggerService.loginFailed(loginDTO.getUsername());
        return ResponseEntity.badRequest().build();
    }
    
}
