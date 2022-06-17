package com.bsep.service.impl;

import com.bsep.dto.TokenDTO;
import com.bsep.model.User;
import com.bsep.security.util.TokenUtils;
import com.bsep.service.AuthenticationService;
import com.bsep.service.UserService;
import com.bsep.exception.CodeNotMatchingException;

import de.taimos.totp.TOTP;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenUtils tokenUtils;
    private final UserService userService;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, TokenUtils tokenUtils, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
        this.userService = userService;
    }

    @Override
    public TokenDTO login(String username, String password, String code) {
    	User user = userService.findByUsername(username);
        Authentication authentication;
        if (user.isUsing2FA() && (code == null || !code.equals(getTOTPCode(user.getSecret())))) {
            throw new CodeNotMatchingException();
        }
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    username, password));
        }catch(Exception ex){
            return null;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User loggedUser = (User) authentication.getPrincipal();
        return new TokenDTO(getToken(loggedUser), loggedUser.getRoles().get(0).getName());
    }

    private String getToken(User user) {
        return tokenUtils.generateToken(user.getUsername(), user.getRoles().get(0).getName());
    }
    
    public static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }
    
    @Override
    public User check2FA(String username, String password) {
    	User user = userService.findByUsername(username);
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    username, password));
        }catch(Exception ex){
            return null;
        }
       return user;
    }
    
}
