package com.sip.controllers;

import com.sip.entities.User;
import com.sip.interfaces.RegisterService;
import com.sip.requests.RegisterRequest;

import com.sip.responses.RegisterResponse;


import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*") 
public class RegisterController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        try {
        	RegisterResponse response = registerService.addUser(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED); 
        } catch (RuntimeException e) {
            return new ResponseEntity<>(
                new RegisterResponse(null, null, null, null,null, e.getMessage()),
                HttpStatus.BAD_REQUEST
            );
        }
    }
    
    @DeleteMapping("/{id}")
    public void delUserNotification(@PathVariable long id) {
    	this.registerService.deleteUserNotification(id);
    }
    
    @GetMapping("/nombreNotifications")
    public int nbreNotifs () {
    	return this.registerService.nombresNotifications();
    }
    
    @GetMapping("/")
    public List<User> getUsers () {
    	return this.registerService.listUsers();
    }
    
    @GetMapping("/allusers")
    public List<User> getusers () {
    	return this.registerService.getUsers();
    }
    
    
    @PutMapping("/{id}")
    public RegisterResponse activeUser(@PathVariable long id) {
    	return this.registerService.activateUser(id);
    }
}

