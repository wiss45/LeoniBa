package com.sip.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sip.entities.User;

public interface UserRepository extends JpaRepository<User,Long> {

	User findByEmail (String email);
	Optional<User> findUserByUsername (String username);

}
