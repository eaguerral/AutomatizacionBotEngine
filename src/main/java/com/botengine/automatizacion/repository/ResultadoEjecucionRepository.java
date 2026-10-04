package com.botengine.automatizacion.repository;

import com.botengine.automatizacion.model.ResultadoEjecucion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResultadoEjecucionRepository
        extends JpaRepository<ResultadoEjecucion, Long> {

    Optional<ResultadoEjecucion>
    findByUuid(String uuid);

    List<ResultadoEjecucion>
    findAllByOrderByFechaCreacionDesc();

    List<ResultadoEjecucion>
    findByUsuarioIdOrderByFechaCreacionDesc(
            Long usuarioId
    );

    List<ResultadoEjecucion>
    findByAutomatizacion_SitioCodigoOrderByFechaCreacionDesc(
            String sitioCodigo
    );
}