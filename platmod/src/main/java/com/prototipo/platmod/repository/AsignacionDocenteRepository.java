package com.prototipo.platmod.repository;

import com.prototipo.platmod.entity.AsignacionDocente;
import com.prototipo.platmod.entity.Curso;
import com.prototipo.platmod.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// AsignacionDocente Repository
@Repository
public interface AsignacionDocenteRepository extends JpaRepository<AsignacionDocente, Long> {
    List<AsignacionDocente> findByCurso(Curso curso);

    List<AsignacionDocente> findByUsuario(Usuario usuario);
}
