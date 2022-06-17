package com.bsep.service;

import com.bsep.dto.TokenDTO;

public interface AuthenticationService {
	TokenDTO login(String username, String password, String code);
	
	Boolean check2FA(String username, String password);
}
