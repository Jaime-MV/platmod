package com.prototipo.platmod.controller;

import com.prototipo.platmod.dto.AuthResponse;
import com.prototipo.platmod.dto.LoginRequest;
import com.prototipo.platmod.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Importante para React
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // --- NUEVO ENDPOINT PARA VERIFICAR CÓDIGO ---
    @PostMapping("/verify")
    public ResponseEntity<?> verificarCodigo(@RequestBody Map<String, String> payload) {
        // Esperamos un JSON { "correo": "...", "codigo": "..." }
        String correo = payload.get("correo");
        String codigo = payload.get("codigo");

        try {
            authService.verificarUsuario(correo, codigo);
            return ResponseEntity.ok(Map.of("mensaje", "Cuenta verificada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}