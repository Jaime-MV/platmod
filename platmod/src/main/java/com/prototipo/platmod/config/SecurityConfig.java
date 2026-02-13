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

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // 1. Accesos Públicos de Autenticación
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()

                        // 2. Accesos Públicos de Lectura (Para el Home Page)
                        .requestMatchers(HttpMethod.GET, "/api/cursos", "/api/cursos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/planes", "/api/planes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/docentes/home").permitAll()

                        // 3. ✅ ZONA PROTEGIDA DE ADMINISTRADOR (NUEVO)
                        // Bloquea cualquier petición que empiece con /api/admin/
                        // si el usuario no tiene el rol ADMINISTRADOR.
                        .requestMatchers("/api/admin/**").hasAuthority("ADMINISTRADOR")

                        // 4. Resto de peticiones (Perfil, clases, etc.) requieren estar logueado
                        .anyRequest().authenticated()
                );

        // NOTA IMPORTANTE: Si estás usando JWT, recuerda que aquí usualmente se agrega:
        // .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // Si ya lo tienes configurado en otro lado o lo omitiste al copiar, está bien.

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