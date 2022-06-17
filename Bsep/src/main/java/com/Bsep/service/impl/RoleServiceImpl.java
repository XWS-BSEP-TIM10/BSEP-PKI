package com.bsep.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bsep.model.Role;
import com.bsep.repository.RoleRepository;
import com.bsep.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

  @Autowired
  private RoleRepository roleRepository;

  @Override
  public Role findById(Long id) {
	Optional<Role> auth = this.roleRepository.findById(id);
	if(auth.isPresent()) return auth.get();
	return null;
  }

  @Override
  public List<Role> findByName(String name) {
	return this.roleRepository.findByName(name);
  }


}
