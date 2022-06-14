package com.Bsep.service;

import java.util.List;


import com.Bsep.dto.ChangePasswordDTO;
import com.Bsep.exception.WrongPasswordException;
import com.Bsep.model.User;

public interface UserService {
    User findById(Long id);
    User findByUsername(String username);
    List<User> findAll ();
    User changePassword(ChangePasswordDTO changePasswordDTO, String username) throws WrongPasswordException;
    User change2FAStatus(String username, Boolean isEnabled);
}
