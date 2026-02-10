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
        // 1. Buscar usuario
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado o credenciales invalidas"));

        // 2. Verificar contrasena
        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new RuntimeException("Contrasena incorrecta");
        }

        // 3. NUEVO: Verificar si la cuenta está activada por correo
        if (!usuario.isCuentaVerificada()) {
            throw new RuntimeException("Tu cuenta no está verificada. Revisa tu correo.");
        }

        // 4. Verificar estado (ban)
        if (!usuario.getEstado()) {
            throw new RuntimeException("El usuario esta inactivo/baneado");
        }

        // 5. Generar respuesta (Aquí iría tu JWT real)
        return AuthResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol())
                .token("AQUI_IRA_TU_TOKEN_JWT_REAL")
                .mensaje("Login exitoso")
                .build();
    }

    // --- NUEVO METODO DE VERIFICACION ---
    public boolean verificarUsuario(String correo, String codigo) {
        Usuario usuario = usuarioRepository.findByCorreoAndCodigoVerificacion(correo, codigo)
                .orElseThrow(() -> new RuntimeException("Codigo invalido o correo incorrecto"));

        // Si lo encuentra, activamos la cuenta
        usuario.setCuentaVerificada(true);
        usuario.setCodigoVerificacion(null); // Limpiamos el codigo para que no se use de nuevo
        usuarioRepository.save(usuario);

        return true;
    }
}