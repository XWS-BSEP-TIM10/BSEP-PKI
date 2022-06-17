package com.bsep.service.impl;

import java.util.List;
import java.util.Optional;

import com.bsep.dto.ChangePasswordDTO;
import com.bsep.exception.UserNotFoundException;
import com.bsep.exception.WrongPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.bsep.model.Role;
import com.bsep.model.User;
import com.bsep.repository.UserRepository;
import com.bsep.service.RoleService;
import com.bsep.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleService roleService;

	@Override
	public User findByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username);
	}

	public User findById(Long id) throws AccessDeniedException {
		return userRepository.findById(id).orElseGet(null);
	}

	public List<User> findAll() throws AccessDeniedException {
		return userRepository.findAll();
	}

	@Override
	public User changePassword(ChangePasswordDTO changePasswordDTO, String username) throws WrongPasswordException {
		User user = findByUsername(username);
		if(user == null)
			throw new UserNotFoundException();
		if(!BCrypt.checkpw(changePasswordDTO.getOldPassword(), user.getPassword()))
			throw new WrongPasswordException();
		return changePassword(user, changePasswordDTO.getNewPassword());
	}

	public User changePassword(User user, String newPassword) {
		user.setPassword(passwordEncoder.encode(newPassword));
		return userRepository.save(user);
	}
	
	@Override
	public User change2FAStatus(String username, Boolean isEnabled) {
		User user = findByUsername(username);
		if(user == null)
			throw new UserNotFoundException();
		user.setUsing2FA(isEnabled);
		return userRepository.save(user);
	}
}
