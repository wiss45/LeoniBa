package com.sip.controllers;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import com.sip.jwt.jwtUtils;
import com.sip.repositories.RoleRepository;
import com.sip.repositories.UserRepository;
import com.sip.requests.LoginRequest;
import com.sip.responses.JwtResponse;
import com.sip.services.UserDetailsImpl;

import jakarta.validation.Valid;


	@CrossOrigin(origins = "*", maxAge = 3600)
	@RestController
	@RequestMapping("/auth")
	public class AuthController {
	  @Autowired
	  AuthenticationManager authenticationManager;

	  @Autowired
	  UserRepository userRepository;

	  @Autowired
	  RoleRepository roleRepository;

	 
	  @Autowired
	  PasswordEncoder encoder;
	  
	  @Autowired
	  jwtUtils jwtUtils;
	  
	  
	  

	  
	  @PostMapping("/login")
	  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		  //1-get Authentication
	    Authentication authentication = authenticationManager.authenticate(
	        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

	     //2-get token
	    SecurityContextHolder.getContext().setAuthentication(authentication);
	    String jwt = jwtUtils.generateJwtToken(authentication);
	    
	    //3-get User details
	    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
	    
	     //4-get roles
	    List<String> roles = userDetails.getAuthorities().stream()
	        .map(item -> item.getAuthority())
	        .collect(Collectors.toList());

	    return ResponseEntity.ok(new JwtResponse(jwt, 
	                         userDetails.getId(), 
	                         userDetails.getUsername(),  
	                         userDetails.getEmail(), 
	                         roles));
	  }

}
