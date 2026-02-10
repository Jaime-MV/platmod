package com.prototipo.platmod.service;

import com.prototipo.platmod.dto.AuthResponse;
import com.prototipo.platmod.dto.LoginRequest;
import com.prototipo.platmod.entity.Usuario;
import com.prototipo.platmod.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        // 1. Buscar usuario por correo
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado o credenciales invalidas"));

        // 2. Verificar contrasena (comparar texto plano vs hash en DB)
        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new RuntimeException("Contrasena incorrecta");
        }

        // 3. Verificar estado
        if (!usuario.getEstado()) {
            throw new RuntimeException("El usuario esta inactivo");
        }

        // 4. Generar respuesta (Aqui generarias el JWT si usaras tokens)
        return AuthResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol())
                .token("TOKEN_DE_PRUEBA_SIMULADO_XYZ") // En el futuro aqui va el JWT real
                .mensaje("Login exitoso")
                .build();
    }
}
