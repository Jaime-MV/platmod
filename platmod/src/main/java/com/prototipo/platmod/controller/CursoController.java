package com.prototipo.platmod.controller;

import com.prototipo.platmod.dto.CursoDTO;
import com.prototipo.platmod.dto.UsuarioResumenDTO;
import com.prototipo.platmod.entity.Curso;
import com.prototipo.platmod.service.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cursos")
@CrossOrigin(origins = "*")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @GetMapping
    public ResponseEntity<List<CursoDTO>> listarCursos() {
        List<Curso> cursos = cursoService.obtenerTodos();

        // Convertimos la lista de entidades a una lista de DTOs
        List<CursoDTO> dtos = cursos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoDTO> obtenerCurso(@PathVariable Long id) {
        Curso curso = cursoService.obtenerPorId(id);
        return ResponseEntity.ok(convertirADTO(curso));
    }

    // Metodo auxiliar para limpiar la informacion y ocultar datos sensibles
    private CursoDTO convertirADTO(Curso curso) {
        return new CursoDTO(
                curso.getIdCurso(),
                curso.getTitulo(),
                curso.getDescripcion(),
                curso.getPortadaUrl()
        );
    }

}