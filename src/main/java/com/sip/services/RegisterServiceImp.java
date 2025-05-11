package com.sip.services;

import org.springframework.stereotype.Service;

import com.sip.entities.Role;
import com.sip.entities.User;
import com.sip.enums.ERole;
import com.sip.interfaces.RegisterService;
import com.sip.repositories.RoleRepository;
import com.sip.repositories.UserRepository;
import com.sip.requests.RegisterRequest;

import com.sip.responses.RegisterResponse;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class RegisterServiceImp implements RegisterService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public RegisterServiceImp(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    
    
    
    @Override
    public RegisterResponse addUser(RegisterRequest request) {
        // Vérifier si l'e-mail est déjà utilisé
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("Cet e-mail est déjà utilisé");
        }

        // Vérifier si le nom d'utilisateur est déjà utilisé
        if (userRepository.findUserByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Ce nom d'utilisateur est déjà pris");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        Set<Role> roles = new HashSet<>();

        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.USER);
            roles.add(userRole);
        } else {
            for (String roleName : request.getRoles()) {
                ERole roleEnum = ERole.valueOf(roleName);
                Role role = roleRepository.findByName(roleEnum);
                roles.add(role);
            }
        }

        user.setRoles(new ArrayList<>(roles));
        user.setEnabled(false);
        User savedUser = userRepository.save(user);

        List<String> roleNames = savedUser.getRoles().stream()
            .map(role -> role.getName().name())
            .collect(Collectors.toList());

        return new RegisterResponse(
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getEmail(),
            roleNames,
            savedUser.getEnabled(),
            "Utilisateur enregistré avec succès"
        );
    }

    
    
  
    @Override
    @Transactional
    public RegisterResponse updateUser(RegisterRequest request) {
        // 1. Récupération du nom d'utilisateur (email) depuis le contexte de sécurité
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Recherche de l'utilisateur par email (authentifié)
        User user = userRepository.findByEmail(currentUsername);
        if (user == null) {
            throw new RuntimeException("Utilisateur authentifié introuvable");
        }

        // 🔐 INFO : La mise à jour se fera en base via l'identifiant interne de l'utilisateur (user.getId())
        // car l'objet 'user' retourné est déjà attaché à l'entity manager JPA.

        // 3. Préservation des anciennes valeurs
        String oldUsername = user.getUsername();
        String oldEmail = user.getEmail();
        Boolean oldEnabled = user.getEnabled();
        List<Role> oldRoles = new ArrayList<>(user.getRoles());
        String oldPassword = user.getPassword();

        // 4. Mise à jour des champs selon la requête
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> roles = request.getRoles().stream()
                .map(roleStr -> {
                    ERole eRole = ERole.valueOf(roleStr);
                    return roleRepository.findByName(eRole);
                })
                .collect(Collectors.toSet());
            user.setRoles(new ArrayList<>(roles));
        }

        // Ne pas modifier 'enabled'
        user.setEnabled(oldEnabled);

        // 5. Sauvegarde en base — via user.getId() implicite
        User updatedUser = userRepository.save(user);

        // 6. Préparation de la réponse
        List<String> roleNames = updatedUser.getRoles().stream()
            .map(role -> role.getName().name())
            .collect(Collectors.toList());

        return new RegisterResponse(
            updatedUser.getId(),
            updatedUser.getUsername(),
            updatedUser.getEmail(),
            roleNames,
            updatedUser.getEnabled(),
            "Profil mis à jour avec succès"
        );
    }

    
    
    
    @Override
    @Transactional
    public void deleteUserNotification(long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        
        List<Role> roles = new ArrayList<>(user.getRoles());
        
       
        for (Role role : roles) {
            role.getUsers().removeIf(u -> u.getId() == user.getId());
            roleRepository.save(role); 
        }
        
       
        user.getRoles().clear();
        userRepository.save(user); 
        
       
        userRepository.delete(user);
    }
    
    @Override
    public int nombresNotifications () {
    	return this.userRepository.nombreNotifications();
    }
    
    @Override
    public List<User> listUsers () {
    	List<User> users = this.userRepository.findByEnabledFalse();
    	return users ;
    }
    
    @Override
    @Transactional
    public RegisterResponse activateUser(long id) {
        User user = this.userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
       
        user.setEnabled(true);
        User savedUser = this.userRepository.save(user);
        
       
        List<String> roleNames = savedUser.getRoles().stream()
            .map(role -> role.getName().name())
            .collect(Collectors.toList());
        
        return new RegisterResponse(
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getEmail(),
            roleNames,
            savedUser.getEnabled(),
            "User activated successfully"
        );
    }
    
    @Override
    public List<User> getUsers(){
    	return this.userRepository.findAll();
    }
    
    
    
}
