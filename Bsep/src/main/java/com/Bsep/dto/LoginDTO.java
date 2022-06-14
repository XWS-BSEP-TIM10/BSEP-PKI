package com.Bsep.dto;

import javax.validation.constraints.NotBlank;

public class LoginDTO {
    @NotBlank(message = "Username is mandatory")
    private String username;
    @NotBlank(message = "Password is mandatory")
    private String password;
    
    private String code;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    
    public String getCode() {
        return code;
    }
}
