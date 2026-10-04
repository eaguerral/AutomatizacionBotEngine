package com.botengine.automatizacion.service;

import com.botengine.automatizacion.dto.AutomatizacionForm;
import com.botengine.automatizacion.model.Automatizacion;
import com.botengine.automatizacion.model.Empresa;
import com.botengine.automatizacion.model.EstadoInsumo;
import com.botengine.automatizacion.model.InsumoAutomatizacion;
import com.botengine.automatizacion.repository.AutomatizacionRepository;
import com.botengine.automatizacion.repository.EmpresaRepository;
import com.botengine.automatizacion.repository.InsumoAutomatizacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublicacionAutomatizacionService {

    private final InsumoAutomatizacionRepository insumoRepository;
    private final EmpresaRepository empresaRepository;
    private final AutomatizacionRepository automatizacionRepository;
    private final AutomatizacionService automatizacionService;

    public PublicacionAutomatizacionService(
            InsumoAutomatizacionRepository insumoRepository,
            EmpresaRepository empresaRepository,
            AutomatizacionRepository automatizacionRepository,
            AutomatizacionService automatizacionService) {

        this.insumoRepository = insumoRepository;
        this.empresaRepository = empresaRepository;
        this.automatizacionRepository = automatizacionRepository;
        this.automatizacionService = automatizacionService;
    }

    @Transactional
    public Automatizacion publicar(
            Long insumoId) {

        InsumoAutomatizacion insumo =
                insumoRepository
                        .findById(insumoId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "La clase de automatización no existe."
                                )
                        );

        if (insumo.getEstado()
                != EstadoInsumo.VINCULADO) {

            throw new IllegalArgumentException(
                    "La clase debe estar vinculada antes de publicarse."
            );
        }

        if (insumo.getRutaClaseGenerada() == null
                || insumo.getRutaClaseGenerada().isBlank()) {

            throw new IllegalArgumentException(
                    "La clase Java vinculada no tiene una ruta válida."
            );
        }

        if (insumo.getRutaXmlGenerada() == null
                || insumo.getRutaXmlGenerada().isBlank()) {

            throw new IllegalArgumentException(
                    "El archivo XML vinculado no tiene una ruta válida."
            );
        }

        Empresa empresa =
                empresaRepository
                        .findAll()
                        .stream()
                        .filter(item ->
                                item.getCarpeta()
                                        .equals(
                                                insumo.getCarpetaEmpresa()
                                        )
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "No existe una empresa asociada a la carpeta de la clase."
                                )
                        );

        if (!empresa.isActivo()) {

            throw new IllegalArgumentException(
                    "La empresa asociada se encuentra inactiva."
            );
        }

        AutomatizacionForm form =
                new AutomatizacionForm();

        form.setCodigo(
                insumo.getCodigo()
        );

        form.setNombre(
                insumo.getNombre()
        );

        form.setDescripcion(
                insumo.getDescripcion()
        );

        form.setSitioCodigo(
                empresa.getCodigo()
        );

        form.setClaseTest(
                insumo.getRutaClaseGenerada()
        );

        form.setArchivoXml(
                insumo.getRutaXmlGenerada()
        );

        form.setActivo(
                true
        );

        return automatizacionRepository
                .findByCodigo(
                        insumo.getCodigo()
                )
                .map(automatizacion ->
                        automatizacionService.actualizar(
                                automatizacion.getId(),
                                form
                        )
                )
                .orElseGet(() ->
                        automatizacionService.crear(
                                form
                        )
                );
    }
}