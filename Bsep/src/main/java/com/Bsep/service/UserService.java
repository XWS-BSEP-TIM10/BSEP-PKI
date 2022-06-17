package com.bsep.service;

import java.util.List;


import com.bsep.dto.ChangePasswordDTO;
import com.bsep.exception.WrongPasswordException;
import com.bsep.model.User;

public interface UserService {
    User findById(Long id);
    User findByUsername(String username);
    List<User> findAll ();
    User changePassword(ChangePasswordDTO changePasswordDTO, String username) throws WrongPasswordException;
    User change2FAStatus(String username, Boolean isEnabled);
}
