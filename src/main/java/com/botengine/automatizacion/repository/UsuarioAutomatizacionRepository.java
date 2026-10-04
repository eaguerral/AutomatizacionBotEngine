package com.botengine.automatizacion.repository;

import com.botengine.automatizacion.model.UsuarioAutomatizacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioAutomatizacionRepository
        extends JpaRepository<UsuarioAutomatizacion, Long> {

    Optional<UsuarioAutomatizacion>
    findByUsuarioIdAndAutomatizacionId(
            Long usuarioId,
            Long automatizacionId
    );

    boolean
    existsByUsuarioIdAndAutomatizacionIdAndActivoTrue(
            Long usuarioId,
            Long automatizacionId
    );

    List<UsuarioAutomatizacion>
    findByUsuarioIdAndActivoTrue(
            Long usuarioId
    );

    List<UsuarioAutomatizacion>
    findByUsuarioIdAndActivoTrueAndAutomatizacionActivoTrueOrderByAutomatizacionNombreAsc(
            Long usuarioId
    );
}