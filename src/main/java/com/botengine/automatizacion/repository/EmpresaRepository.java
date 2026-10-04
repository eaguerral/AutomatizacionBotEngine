package com.botengine.automatizacion.repository;

import com.botengine.automatizacion.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpresaRepository
        extends JpaRepository<Empresa, Long> {

    Optional<Empresa> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(
            String codigo,
            Long id
    );

    boolean existsByCarpeta(String carpeta);

    boolean existsByCarpetaAndIdNot(
            String carpeta,
            Long id
    );

    List<Empresa> findAllByOrderByNombreAsc();

    List<Empresa> findByActivoTrueOrderByNombreAsc();
}