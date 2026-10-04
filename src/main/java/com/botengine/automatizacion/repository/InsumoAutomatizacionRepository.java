package com.botengine.automatizacion.repository;

import com.botengine.automatizacion.model.InsumoAutomatizacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsumoAutomatizacionRepository
        extends JpaRepository<InsumoAutomatizacion, Long> {

    boolean existsByCodigo(String codigo);

    boolean existsByCarpetaEmpresa(String carpetaEmpresa);

    List<InsumoAutomatizacion>
    findAllByOrderByFechaCreacionDesc();
}