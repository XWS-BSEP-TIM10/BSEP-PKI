package com.Bsep.controller;

import com.Bsep.dto.LoginDTO;
import com.Bsep.dto.TokenDTO;
import com.Bsep.service.AuthenticationService;
import com.Bsep.service.LoggerService;
import com.Bsep.service.impl.LoggerServiceImpl;
import com.Bsep.exception.CodeNotMatchingException;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid LoginDTO loginDTO, HttpServletRequest request) {
    	try {
        TokenDTO tokenDTO = authenticationService.login(loginDTO.getUsername(), loginDTO.getPassword(), loginDTO.getCode());
        if(tokenDTO != null) {
            loggerService.loginSuccess(loginDTO.getUsername(), request.getRemoteAddr());
            return ResponseEntity.ok(tokenDTO);
        }
        loggerService.loginFailed(loginDTO.getUsername(), request.getRemoteAddr());
        return ResponseEntity.badRequest().build();
    	}
    	catch(CodeNotMatchingException codeNotMatchingException){
    		loggerService.loginFailedCodeNotMatching(loginDTO.getUsername(),  request.getRemoteAddr());
    		return ResponseEntity.status(300).build();
    	}
    }
    
    @PostMapping(value = "/login-check")
    public ResponseEntity<Boolean> login2FACheck(@RequestBody @Valid LoginDTO loginDTO, HttpServletRequest request) {
        Boolean isEnabled2FA = authenticationService.check2FA(loginDTO.getUsername(), loginDTO.getPassword());
        if(isEnabled2FA != null) {
        	loggerService.login2FACheck(loginDTO.getUsername(), request.getRemoteAddr());
            return ResponseEntity.ok(isEnabled2FA);
        }
        loggerService.login2FACheckFailed(loginDTO.getUsername(), request.getRemoteAddr());
        return ResponseEntity.badRequest().build();
    
    }
    
}
