package com.prototipo.platmod.repository;

import com.prototipo.platmod.dto.DocenteHomeDTO;
import com.prototipo.platmod.entity.Docente;
import com.prototipo.platmod.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import com.prototipo.platmod.entity.Docente;
import com.prototipo.platmod.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Docente Repository
@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {
    Optional<Docente> findByUsuario(Usuario usuario);

    List<Docente> findByEstadoDocente(Boolean estadoDocente);

    // --- NUEVO MÉTODO PARA EL HOME (Agrégalo aquí) ---
    // Esta consulta trae: Nombre, Especialidad, Foto (del perfil) y un Curso
    // asignado
    @Query("""
                SELECT new com.prototipo.platmod.dto.DocenteHomeDTO(
                    d.idDocente,
                    u.nombre,
                    d.especialidad,
                    p.foto,
                    (SELECT c.titulo
                     FROM AsignacionDocente ad
                     JOIN ad.curso c
                     WHERE ad.usuario.idUsuario = u.idUsuario
                     ORDER BY ad.idAsignacion ASC LIMIT 1)
                )
                FROM Docente d
                JOIN d.usuario u
                LEFT JOIN PerfilDetalle p ON p.usuario.idUsuario = u.idUsuario
                WHERE d.estadoDocente = true
            """)
    List<DocenteHomeDTO> obtenerDocentesHome();
    @Query(value = """
            SELECT ad.id_asignacion,
                   u.nombre,
                   u.correo,
                   pd.foto,
                   d.especialidad,
                   c.titulo AS curso_titulo
            FROM asignacion_docente ad
            JOIN usuario u ON ad.id_usuario = u.id_usuario
            JOIN docente d ON u.id_usuario = d.id_usuario
            LEFT JOIN perfil_detalle pd ON u.id_usuario = pd.id_usuario
            JOIN curso c ON ad.id_curso = c.id_curso
            WHERE (ad.id_asignacion, ad.id_usuario) IN (
                SELECT MIN(ad2.id_asignacion), ad2.id_usuario
                FROM asignacion_docente ad2
                GROUP BY ad2.id_usuario
            )
            ORDER BY ad.id_asignacion ASC
            """, nativeQuery = true)
    List<Object[]> obtenerDocentesHome();
}
