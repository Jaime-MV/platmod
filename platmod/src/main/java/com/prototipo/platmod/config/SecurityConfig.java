package com.prototipo.platmod.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Se mantiene tu config de CORS
                .authorizeHttpRequests(auth -> auth
                        // 1. 🔥 REGLA DE ORO PARA REACT: Permitir preflight (OPTIONS)
                        // Si no pones esto, el navegador se bloquea antes de enviar el login
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. Login y Registro Públicos
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()

                        // 3. Lecturas Públicas
                        .requestMatchers(HttpMethod.GET, "/api/cursos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/planes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/docentes/home").permitAll()

                        // 4. Admin
                        .requestMatchers("/api/admin/**").hasAuthority("ADMINISTRADOR")

                        // 5. El resto bloqueado
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ✅ Configuración permisiva para desarrollo
        configuration.addAllowedOriginPattern("*"); // Permite peticiones desde localhost o tu dominio
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}