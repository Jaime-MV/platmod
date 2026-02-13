package com.prototipo.platmod.controller;

import com.prototipo.platmod.entity.*;
import com.prototipo.platmod.repository.*;
import com.prototipo.platmod.service.AsignacionDocenteService; // <--- Importamos el servicio
import com.prototipo.platmod.service.PlanSuscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired private CursoRepository cursoRepository;
    @Autowired private PlanSuscripcionService planService;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private AsignacionDocenteService asignacionService;

    // --- GESTION DE PLANES ---
    @PutMapping("/planes/{id}")
    public ResponseEntity<PlanSuscripcion> actualizarPlan(@PathVariable Long id, @RequestBody PlanSuscripcion planDetalles) {
        try {
            return ResponseEntity.ok(planService.actualizar(id, planDetalles));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- GESTION DE CURSOS ---

    // 1. CORRECCIÓN DEL ORDEN: Agregamos Sort.by
    @GetMapping("/cursos")
    public List<Curso> listarCursosAdmin() {
        return cursoRepository.findAll(Sort.by(Sort.Direction.ASC, "idCurso"));
    }

    @PostMapping("/cursos")
    public Curso crearCurso(@RequestBody Curso curso) {
        return cursoRepository.save(curso);
    }

    @PutMapping("/cursos/{id}")
    public ResponseEntity<Curso> editarCurso(@PathVariable Long id, @RequestBody Curso cursoDetalles) {
        return cursoRepository.findById(id).map(curso -> {
            // Solo actualizamos si el dato viene en el JSON (evita nulos accidentales)
            if (cursoDetalles.getTitulo() != null) curso.setTitulo(cursoDetalles.getTitulo());
            if (cursoDetalles.getDescripcion() != null) curso.setDescripcion(cursoDetalles.getDescripcion());
            if (cursoDetalles.getPortadaUrl() != null) curso.setPortadaUrl(cursoDetalles.getPortadaUrl());

            // Log para depurar en consola de Render/Local
            if (cursoDetalles.getEstado() != null) {
                System.out.println("Actualizando estado curso ID " + id + " a: " + cursoDetalles.getEstado());
                curso.setEstado(cursoDetalles.getEstado());
            }

            return ResponseEntity.ok(cursoRepository.save(curso));
        }).orElse(ResponseEntity.notFound().build());
    }

    // --- ASIGNACIÓN DE DOCENTES ---
    @PostMapping("/cursos/{idCurso}/asignar-docente/{idUsuario}")
    public ResponseEntity<?> asignarDocente(@PathVariable Long idCurso, @PathVariable Long idUsuario) {
        // 1. Buscamos las entidades
        Curso curso = cursoRepository.findById(idCurso)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        Usuario docente = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));

        // 2. Crear la relación
        AsignacionDocente nuevaAsignacion = new AsignacionDocente();
        nuevaAsignacion.setCurso(curso);
        nuevaAsignacion.setUsuario(docente);

        // 3. Guardar usando el servicio
        AsignacionDocente guardado = asignacionService.crear(nuevaAsignacion);

        return ResponseEntity.ok(guardado);
    }

    @GetMapping("/docentes-list")
    public List<Usuario> listarDocentes() {
        // Usamos el enum que está dentro de tu clase Usuario
        return usuarioRepository.findByRol(Usuario.Rol.DOCENTE);
    }
}