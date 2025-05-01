package com.sip.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sip.entities.Role;
import com.sip.enums.ERole;

public interface RoleRepository extends JpaRepository<Role,Long> {
	
	Role findByName (ERole name);

}
