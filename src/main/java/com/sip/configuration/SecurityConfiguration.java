package com.sip.configuration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.sip.jwt.AuthEntryPointJwt;
import com.sip.jwt.AuthTokenFilter;
import com.sip.services.UserDetailsServiceImpl;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {
  
    // Injection des services nécessaires
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    /**
     * Bean pour le filtre d'authentification JWT
     * Ce filtre va intercepter les requêtes entrantes pour valider le token JWT.
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * Bean pour la configuration CORS (Cross-Origin Resource Sharing).
     * Ici, on définit les règles de partage entre le frontend Angular (port 4200) et le backend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // On permet les origines spécifiques (par exemple, http://localhost:4200 pour Angular)
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200")); 
        // On définit les méthodes HTTP autorisées (GET, POST, etc.)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // On permet tous les en-têtes
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // Permet l'envoi de cookies et de credentials
        configuration.setAllowCredentials(true);
        
        // Enregistrement de la configuration CORS
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    /**
     * Bean pour l'authentification basée sur l'utilisateur.
     * Ce provider permet de valider un utilisateur en utilisant le service de détails utilisateur personnalisé 
     * (UserDetailsServiceImpl) et l'encodeur de mots de passe.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        // Définition du service qui récupère les informations sur l'utilisateur
        authProvider.setUserDetailsService(userDetailsService);
        // Définition de l'encodeur de mot de passe pour sécuriser les mots de passe
        authProvider.setPasswordEncoder(passwordEncoder());
        
        return authProvider;
    }

    /**
     * Bean pour le gestionnaire d'authentification.
     * Il est utilisé pour récupérer le gestionnaire d'authentification de Spring Security.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Bean pour l'encodeur de mot de passe.
     * On utilise BCrypt pour encoder les mots de passe (recommandé pour une meilleure sécurité).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuration de sécurité HTTP (filtrage des requêtes).
     * Ici, on définit les règles pour les requêtes HTTP entrantes et les protections associées.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // On désactive la protection CSRF, généralement pour les API REST
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Activation du CORS
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Handler des erreurs d'authentification
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // On désactive la gestion d'état de session, car on utilise un JWT stateless
            .authorizeHttpRequests(auth -> 
                // Autoriser l'accès à certaines routes sans authentification (par exemple, /auth/**)
                auth.requestMatchers("/auth/**").permitAll() 
                    // Autoriser l'accès à /users/ sans authentification
                    .requestMatchers("/users/**").permitAll()
                    
                    // Toute autre requête nécessite une authentification
                    .anyRequest().authenticated()
            );
        
        // Enregistrement du provider d'authentification personnalisé
        http.authenticationProvider(authenticationProvider());

        // On ajoute le filtre d'authentification JWT avant le filtre d'authentification de base
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    /**
     * Bean pour la configuration des règles de sécurité au niveau des ressources statiques (images, CSS, etc.).
     * Ces ressources sont ignorées par la sécurité pour éviter de bloquer l'accès aux fichiers statiques.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() throws Exception {
        return (web) -> web.ignoring().requestMatchers("/css/**", "/images/**","/uploads/**");
    }

}
