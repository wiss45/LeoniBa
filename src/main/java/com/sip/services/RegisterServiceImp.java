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
}
