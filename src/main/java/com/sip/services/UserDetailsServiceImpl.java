package com.sip.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sip.entities.User;
import com.sip.repositories.UserRepository;

/**
 * Implémentation personnalisée de l'interface UserDetailsService de Spring Security.
 * Cette classe est responsable du chargement d'un utilisateur depuis la base de données
 * à partir de son email (ou nom d'utilisateur selon ton choix).
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Cette méthode est automatiquement appelée par Spring Security
     * lorsque quelqu’un tente de se connecter.
     * Elle charge les détails de l'utilisateur à partir de son email.
     *
     * @param email : l'identifiant de l'utilisateur (ici, on utilise l'email comme username)
     * @return un objet UserDetails utilisé par Spring Security pour l'authentification
     * @throws UsernameNotFoundException si aucun utilisateur n'est trouvé avec cet email
     */
    @Override
    @Transactional // Garantit que cette opération est faite dans une transaction
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Recherche l'utilisateur dans la base de données par email
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User Not Found with username: " + username
                ));

        // Construit un objet UserDetailsImpl à partir de l'entité User trouvée
        return UserDetailsImpl.build(user);
    }
}


/*
Ce qu’il faut retenir :
UserDetailsService est obligatoire pour Spring Security quand tu veux une authentification personnalisée.

Ici, l'utilisateur est chargé à partir de l’email avec findUserByEmail().

Le résultat est un objet UserDetailsImpl, qui permet à Spring de vérifier le mot de passe, les rôles, etc.
*/