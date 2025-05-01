package com.sip.controllers;

import com.sip.interfaces.RegisterService;
import com.sip.requests.RegisterRequest;

import com.sip.responses.RegisterResponse;


import jakarta.validation.Valid;

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
}

