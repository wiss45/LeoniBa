package com.sip.services;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sip.entities.User;

/**
 * Classe qui implémente l'interface UserDetails de Spring Security.
 * Elle sert à encapsuler les informations de l'utilisateur pour l'authentification.
 */
public class UserDetailsImpl implements UserDetails {
  
  private static final long serialVersionUID = 1L;

  private Long id;
  private String username;
  private String email;

  @JsonIgnore // Évite que le mot de passe soit exposé dans les réponses JSON
  private String password;

  // Représente les rôles/autorités de l'utilisateur (ex: ROLE_USER, ROLE_ADMIN)
  private Collection<? extends GrantedAuthority> authorities;

  /**
   * Constructeur de la classe
   */
  public UserDetailsImpl(Long id, String username, String email, String password,
                         Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
    this.authorities = authorities;
  }

  /**
   * Méthode utilitaire qui transforme un objet `User` en `UserDetailsImpl`.
   * Elle convertit les rôles de l'utilisateur en autorités reconnues par Spring Security.
   */
  public static UserDetailsImpl build(User user) {
    List<GrantedAuthority> authorities = user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority(role.getName().name()))
        .collect(Collectors.toList());

    return new UserDetailsImpl(
        user.getId(), 
        user.getUsername(), 
        user.getEmail(),
        user.getPassword(), 
        authorities);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  // Ces méthodes permettent à Spring Security de vérifier l’état du compte utilisateur
  @Override
  public boolean isAccountNonExpired() {
    return true; // Le compte n'est jamais expiré
  }

  @Override
  public boolean isAccountNonLocked() {
    return true; // Le compte n'est jamais verrouillé
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true; // Le mot de passe n'est jamais expiré
  }

  @Override
  public boolean isEnabled() {
    return true; // Le compte est activé
  }

  /**
   * Vérifie l'égalité entre deux objets UserDetailsImpl
   */
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  }

  /**
   * Méthode optionnelle pour récupérer un champ non utilisé ici (ex: nom de famille)
   */
  public String getLastname() {
    return null; // À implémenter si nécessaire
  }
}


/*
Pourquoi cette classe est importante ?
Spring Security utilise cette classe pour :

Authentifier l'utilisateur (via UserDetailsService)

Vérifier ses rôles (grâce à getAuthorities)

Protéger certaines routes via hasRole, hasAuthority, etc.
*/