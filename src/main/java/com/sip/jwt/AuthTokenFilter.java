package com.sip.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sip.services.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Ce filtre s'exécute une seule fois par requête (d'où OncePerRequestFilter)
public class AuthTokenFilter extends OncePerRequestFilter {

  // Injecte l'utilitaire JWT pour générer et valider les tokens
  @Autowired
  private jwtUtils jwtUtils;

  // Service personnalisé pour charger les détails de l'utilisateur (depuis la BDD)
  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  // Logger pour afficher les erreurs ou informations utiles
  private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

  /**
   * Méthode appelée à chaque requête HTTP entrante.
   * Objectif : vérifier si un token JWT est présent et valide, puis authentifier l'utilisateur.
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      // Récupère le token JWT de l'en-tête Authorization
      String jwt = parseJwt(request);

      // Vérifie que le token est présent et valide
      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
        // Extrait le nom d'utilisateur depuis le token
        String username = jwtUtils.getUserNameFromJwtToken(jwt);

        // Charge les détails de l'utilisateur depuis la base de données
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Crée un objet d'authentification Spring Security avec les rôles de l'utilisateur
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails, // principal (utilisateur)
                null, // credentials (mot de passe, ici non requis)
                userDetails.getAuthorities() // rôles/permissions
            );

        // Ajoute les détails de la requête (comme l'adresse IP) à l'objet auth
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Insère l'objet auth dans le contexte de sécurité actuel
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) {
      logger.error("Cannot set user authentication: {}", e); // En cas d'erreur, log mais continue le traitement
    }

    // Passe la main au filtre suivant dans la chaîne (ou au contrôleur)
    filterChain.doFilter(request, response);
  }

  /**
   * Extrait le token JWT de l'en-tête HTTP "Authorization"
   * Le format attendu est : "Bearer <token>"
   */
  private String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");

    // Vérifie que le header commence bien par "Bearer " et qu’il n’est pas vide
    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7); // Extrait juste la partie token
    }

    return null; // Aucun token présent
  }
}


/*
🔐 Résumé du rôle de AuthTokenFilter :
Il intercepte chaque requête HTTP.

Il cherche un token JWT dans l’en-tête Authorization.

S’il y a un token valide :

Il extrait le username,

Charge les rôles via UserDetailsServiceImpl,

Authentifie l'utilisateur en remplissant le SecurityContextHolder.
*/