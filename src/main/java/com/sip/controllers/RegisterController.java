package com.sip.controllers;

import com.sip.entities.User;
import com.sip.interfaces.RegisterService;
import com.sip.repositories.UserRepository;
import com.sip.requests.RegisterRequest;

import com.sip.responses.RegisterResponse;


import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*") 
public class RegisterController {

    private final RegisterService registerService;

    
    @Autowired 
    private UserRepository userRepository ;
    
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
    
    @PutMapping("/update/me")
    public ResponseEntity<RegisterResponse> updateCurrentUser(@Valid @RequestBody RegisterRequest request) {
        try {
            // Mise à jour de l'utilisateur dans le service
            RegisterResponse updatedUser = registerService.updateUser(request);
            // Retourne une réponse avec statut OK
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Gérer les erreurs et retourner un message détaillé
            return new ResponseEntity<>(
                new RegisterResponse(null, null, null, null, null, "Erreur: " + e.getMessage()),
                HttpStatus.BAD_REQUEST
            );
        }
    }


    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        // Récupérer le nom d'utilisateur (username) à partir du contexte de sécurité
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Chercher l'utilisateur par son nom d'utilisateur
        Optional<User> userOptional = userRepository.findUserByUsername(username); // Utilise findUserByUsername

        // Vérifie si l'utilisateur existe
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Retourne 403 si l'utilisateur n'est pas trouvé
        }

        // Retourne l'utilisateur trouvé avec un statut OK
        return ResponseEntity.ok(userOptional.get());
    }



}

