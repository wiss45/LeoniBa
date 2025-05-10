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
    	
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("Email already in use");
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
        System.out.println(savedUser);
        List<String> roleNames = savedUser.getRoles()
                                          .stream()
                                          .map(role -> role.getName().name())
                                          .collect(Collectors.toList());

        return new RegisterResponse(
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getEmail(),
            roleNames,
            savedUser.getEnabled(),
            "User registered successfully"
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
