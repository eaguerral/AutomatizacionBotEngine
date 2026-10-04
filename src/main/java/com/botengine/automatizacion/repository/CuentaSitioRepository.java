package com.botengine.automatizacion.repository;

import com.botengine.automatizacion.model.CuentaSitio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CuentaSitioRepository
        extends JpaRepository<CuentaSitio, Long> {

    List<CuentaSitio>
    findBySitioCodigoAndActivoTrueOrderByPrioridadAscIdAsc(
            String sitioCodigo
    );

    List<CuentaSitio>
    findAllByOrderBySitioCodigoAscPrioridadAscIdAsc();

    boolean existsBySitioCodigoAndAlias(
            String sitioCodigo,
            String alias
    );

    boolean existsBySitioCodigoAndAliasAndIdNot(
            String sitioCodigo,
            String alias,
            Long id
    );
}