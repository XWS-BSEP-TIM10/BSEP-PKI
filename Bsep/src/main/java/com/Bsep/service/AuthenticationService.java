package com.bsep.service;

import com.bsep.dto.TokenDTO;
import com.bsep.model.User;

public interface AuthenticationService {
	TokenDTO login(String username, String password, String code);
	
	User check2FA(String username, String password);
}
