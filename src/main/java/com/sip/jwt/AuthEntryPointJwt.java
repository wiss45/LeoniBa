package com.sip.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Cette classe est appelée automatiquement lorsqu'un utilisateur tente d'accéder à une ressource sécurisée sans être authentifié.
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

  // Logger pour afficher l’erreur dans les logs serveur
  private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

  /**
   * Cette méthode est déclenchée automatiquement lorsque l'utilisateur n'est pas autorisé à accéder à une ressource protégée.
   * Elle renvoie une réponse JSON personnalisée avec un code 401 (Unauthorized).
   */
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
                       AuthenticationException authException) throws IOException, ServletException {

    // Log l'erreur dans la console pour aider au debug
    logger.error("Unauthorized error: {}", authException.getMessage());

    // Spécifie que la réponse sera en JSON
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    // Définit le code HTTP comme "401 Unauthorized"
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    // Prépare le corps de la réponse JSON
    final Map<String, Object> body = new HashMap<>();
    body.put("status", HttpServletResponse.SC_UNAUTHORIZED); // Code 401
    body.put("error", "Vous devez vous connecter"); // Message personnalisé
    body.put("message", authException.getMessage()); // Détail technique de l'erreur
    body.put("path", request.getServletPath()); // Le chemin qui a provoqué l'erreur

    // Écrit le corps de la réponse JSON dans le flux de sortie
    final ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(response.getOutputStream(), body);
  }
}


/*
À quoi sert cette classe ?
Elle intercepte les erreurs d'accès non autorisé :

Quand un utilisateur non connecté tente d'accéder à une URL sécurisée,

Elle retourne automatiquement un message 401 Unauthorized bien formaté (en JSON).


*/