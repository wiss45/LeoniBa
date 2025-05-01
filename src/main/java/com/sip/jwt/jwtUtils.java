// Paquet contenant les outils JWT
package com.sip.jwt;

import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.sip.services.UserDetailsImpl;

import io.jsonwebtoken.*; // Librairie pour manipuler les JWT
import io.jsonwebtoken.security.Keys;

@Component // Marque cette classe comme un composant Spring (injection possible)
public class jwtUtils {

  // Logger pour journaliser les erreurs ou infos (bon pour le debug)
  private static final Logger logger = LoggerFactory.getLogger(jwtUtils.class);

  // Clé secrète (injectée depuis application.properties)
  @Value("${jwtSecret}")
  private String jwtSecret;

  // Durée d’expiration du token (injectée depuis application.properties)
  @Value("${jwtExpirationMs}")
  private int jwtExpirationMs;

  /**
   * Génère un JWT à partir d’un objet Authentication (l'utilisateur connecté)
   */
  public String generateJwtToken(Authentication authentication) {
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal(); // récupère les infos de l'utilisateur

    return Jwts.builder()
        .setSubject(userPrincipal.getUsername()) // définit le sujet du token (ici le username)
        //.claim("email", userPrincipal.getEmail()) // tu pourrais ajouter des infos personnalisées (claims)
        //.claim("password", userPrincipal.getPassword()) // ⚠️ ne jamais inclure le mot de passe !
        //.claim("roles", userPrincipal.getAuthorities()) // utile si tu veux stocker les rôles
        .setIssuedAt(new Date()) // date de création du token
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // date d'expiration
        .signWith(key(), SignatureAlgorithm.HS256) // signature avec clé secrète + algo HS256
        .compact(); // génère le token final
  }

  /**
   * Crée une clé à partir de la chaîne secrète (elle est convertie en byte[])
   * Cela permet d’avoir une clé compatible avec l’algorithme HS256
   */
  private Key key() {
    byte[] secret = jwtSecret.getBytes(); // conversion string -> byte[]
    return Keys.hmacShaKeyFor(secret); // clé HMAC compatible avec HS256
  }

  /**
   * Extrait le nom d'utilisateur à partir d’un token JWT
   */
  public String getUserNameFromJwtToken(String token) {
    return Jwts.parserBuilder()
               .setSigningKey(key()) // utilise la même clé que celle de la signature
               .build()
               .parseClaimsJws(token) // vérifie et décode le token
               .getBody()
               .getSubject(); // récupère le sujet (username)
  }

  /**
   * Vérifie que le token JWT est valide
   */
  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken); // essaie de parser (donc valider) le token
      return true;
    } catch (MalformedJwtException e) { // mauvais format
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) { // expiré
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) { // non supporté (mauvais algo, etc.)
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) { // vide ou invalide
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false; // si une exception est levée, le token est invalide
  }
}

/*
 generateJwtToken() : génère un token JWT sécurisé avec une date d’expiration.

getUserNameFromJwtToken() : récupère le username encodé dans le token.

validateJwtToken() : vérifie la validité du token (format, expiration, etc).

key() : crée une clé HMAC à partir de ta jwtSecret.


 */
