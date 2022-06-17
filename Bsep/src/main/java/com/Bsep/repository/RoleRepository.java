package com.bsep.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bsep.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	List<Role> findByName(String name);
}
