package com.botengine.automatizacion.repository;

import com.botengine.automatizacion.model.Automatizacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AutomatizacionRepository
        extends JpaRepository<Automatizacion, Long> {

    Optional<Automatizacion> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByClaseTest(String claseTest);

    boolean existsByCodigoAndIdNot(
            String codigo,
            Long id
    );

    boolean existsByClaseTestAndIdNot(
            String claseTest,
            Long id
    );

    List<Automatizacion>
    findByActivoTrueOrderByNombreAsc();
}