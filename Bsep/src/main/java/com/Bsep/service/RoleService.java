package com.bsep.service;

import java.util.List;

import com.bsep.model.Role;

public interface RoleService {
	Role findById(Long id);
	List<Role> findByName(String name);
}
