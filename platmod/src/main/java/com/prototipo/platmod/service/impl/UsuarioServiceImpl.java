package com.prototipo.platmod.service.impl;

import com.prototipo.platmod.entity.Usuario;
import com.prototipo.platmod.repository.UsuarioRepository;
import com.prototipo.platmod.service.EmailService; // IMPORTANTE: Importar tu servicio de email
import com.prototipo.platmod.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Random; // IMPORTANTE: Para generar el codigo aleatorio

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; // 1. INYECTAMOS EL SERVICIO DE EMAIL

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + correo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerPorRol(Usuario.Rol rol) {
        return usuarioRepository.findByRol(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerPorEstado(Boolean estado) {
        return usuarioRepository.findByEstado(estado);
    }

    @Override
    public Usuario crear(Usuario usuario) {
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("El correo ya esta registrado: " + usuario.getCorreo());
        }

        // 2. ENCRIPTAR LA CONTRASENA
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

        // 3. GENERAR CODIGO DE VERIFICACION (6 Digitos)
        String codigo = String.valueOf(new Random().nextInt(900000) + 100000);
        usuario.setCodigoVerificacion(codigo);

        // 4. MARCAR COMO NO VERIFICADO
        usuario.setCuentaVerificada(false);

        // 5. GUARDAR EN BASE DE DATOS
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // 6. ENVIAR EL CORREO
        emailService.enviarCodigoVerificacion(usuario.getCorreo(), codigo);

        return usuarioGuardado;
    }

    @Override
    public Usuario actualizar(Long id, Usuario usuarioActualizado) {
        Usuario usuario = obtenerPorId(id);

        // Verificar si el correo ya existe (excepto el del mismo usuario)
        if (!usuario.getCorreo().equals(usuarioActualizado.getCorreo())
                && usuarioRepository.existsByCorreo(usuarioActualizado.getCorreo())) {
            throw new RuntimeException("El correo ya esta registrado: " + usuarioActualizado.getCorreo());
        }

        usuario.setNombre(usuarioActualizado.getNombre());
        usuario.setCorreo(usuarioActualizado.getCorreo());

        // OJO: Si actualizan contrasena, lo ideal es encriptarla de nuevo.
        // He agregado la encriptacion aqui por seguridad, si no quieres, quita el .encode()
        if (usuarioActualizado.getContrasena() != null && !usuarioActualizado.getContrasena().isEmpty()) {
            usuario.setContrasena(passwordEncoder.encode(usuarioActualizado.getContrasena()));
        }

        usuario.setRol(usuarioActualizado.getRol());
        usuario.setEstado(usuarioActualizado.getEstado());

        return usuarioRepository.save(usuario);
    }

    @Override
    public void eliminar(Long id) {
        Usuario usuario = obtenerPorId(id);
        usuarioRepository.delete(usuario);
    }

    @Override
    public void cambiarEstado(Long id, Boolean estado) {
        Usuario usuario = obtenerPorId(id);
        usuario.setEstado(estado);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }
}