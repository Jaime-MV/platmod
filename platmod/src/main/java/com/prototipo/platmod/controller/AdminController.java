package com.prototipo.platmod.controller;

import com.prototipo.platmod.entity.*;
import com.prototipo.platmod.repository.*;
import com.prototipo.platmod.service.AsignacionDocenteService; // <--- Importamos el servicio
import com.prototipo.platmod.service.PlanSuscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired private CursoRepository cursoRepository;
    @Autowired private PlanSuscripcionService planService;
    @Autowired private UsuarioRepository usuarioRepository;

    // CAMBIO: Inyectamos el Servicio, no el Repositorio
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
    @PostMapping("/cursos")
    public Curso crearCurso(@RequestBody Curso curso) {
        return cursoRepository.save(curso);
    }

    @PutMapping("/cursos/{id}")
    public ResponseEntity<Curso> editarCurso(@PathVariable Long id, @RequestBody Curso cursoDetalles) {
        return cursoRepository.findById(id).map(curso -> {
            curso.setTitulo(cursoDetalles.getTitulo());
            curso.setDescripcion(cursoDetalles.getDescripcion());
            curso.setPortadaUrl(cursoDetalles.getPortadaUrl());
            // Validamos que venga el estado para no poner nulos
            if (cursoDetalles.getEstado() != null) {
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
}