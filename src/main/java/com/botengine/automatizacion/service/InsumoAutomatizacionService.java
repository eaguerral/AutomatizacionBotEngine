package com.botengine.automatizacion.service;

import com.botengine.automatizacion.dto.InsumoAutomatizacionForm;
import com.botengine.automatizacion.model.Empresa;
import com.botengine.automatizacion.model.EstadoInsumo;
import com.botengine.automatizacion.model.InsumoAutomatizacion;
import com.botengine.automatizacion.repository.EmpresaRepository;
import com.botengine.automatizacion.repository.InsumoAutomatizacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class InsumoAutomatizacionService {

    private final InsumoAutomatizacionRepository repository;
    private final EmpresaRepository empresaRepository;
    private final RecursoAutomatizacionDetectorService detectorService;

    public InsumoAutomatizacionService(
            InsumoAutomatizacionRepository repository,
            EmpresaRepository empresaRepository,
            RecursoAutomatizacionDetectorService detectorService) {

        this.repository = repository;
        this.empresaRepository = empresaRepository;
        this.detectorService = detectorService;
    }

    public List<InsumoAutomatizacion> listarTodos() {

        return repository
                .findAllByOrderByFechaCreacionDesc();
    }

    public InsumoAutomatizacion buscarPorId(
            Long id) {

        return repository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La clase no existe."
                        )
                );
    }

    public InsumoAutomatizacionForm obtenerFormulario(
            Long id) {

        InsumoAutomatizacion insumo =
                buscarPorId(id);

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
                        .orElse(null);

        InsumoAutomatizacionForm form =
                new InsumoAutomatizacionForm();

        form.setId(
                insumo.getId()
        );

        form.setCodigo(
                insumo.getCodigo()
        );

        form.setNombre(
                insumo.getNombre()
        );

        form.setDescripcion(
                insumo.getDescripcion()
        );

        if (empresa != null) {

            form.setEmpresaId(
                    empresa.getId()
            );
        }

        return form;
    }

    public InsumoAutomatizacion guardar(
            InsumoAutomatizacionForm form) {

        String codigo =
                obligatorio(
                        form.getCodigo(),
                        "El código es obligatorio."
                )
                        .replace(" ", "_")
                        .replace("-", "_")
                        .toUpperCase(Locale.ROOT);

        String nombre =
                obligatorio(
                        form.getNombre(),
                        "El nombre es obligatorio."
                );

        if (form.getEmpresaId() == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar una empresa."
            );
        }

        Empresa empresa =
                empresaRepository
                        .findById(
                                form.getEmpresaId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "La empresa seleccionada no existe."
                                )
                        );

        if (!empresa.isActivo()) {

            throw new IllegalArgumentException(
                    "La empresa seleccionada está inactiva."
            );
        }

        InsumoAutomatizacion insumo;

        if (form.getId() == null) {

            if (repository.existsByCodigo(
                    codigo
            )) {

                throw new IllegalArgumentException(
                        "Ya existe una clase con ese código."
                );
            }

            insumo =
                    new InsumoAutomatizacion();

            insumo.setEstado(
                    EstadoInsumo.PENDIENTE
            );

        } else {

            insumo =
                    buscarPorId(
                            form.getId()
                    );
        }

        insumo.setCodigo(
                codigo
        );

        insumo.setNombre(
                nombre
        );

        insumo.setCarpetaEmpresa(
                empresa.getCarpeta()
        );

        insumo.setDescripcion(
                opcional(
                        form.getDescripcion()
                )
        );

        return repository.save(
                insumo
        );
    }

    public InsumoAutomatizacion vincularRecursos(
            Long id,
            String clase,
            String archivoXml) {

        InsumoAutomatizacion insumo =
                buscarPorId(id);

        String carpeta =
                insumo.getCarpetaEmpresa();

        detectorService
                .validarClaseDetectada(
                        carpeta,
                        clase
                );

        detectorService
                .validarXmlDetectado(
                        carpeta,
                        archivoXml
                );

        String rutaClase =
                detectorService
                        .generarRutaClase(
                                carpeta,
                                clase
                        );

        String rutaXml =
                detectorService
                        .generarRutaXml(
                                carpeta,
                                archivoXml
                        );

        insumo.setClaseDetectada(
                clase
        );

        insumo.setArchivoXmlDetectado(
                archivoXml
        );

        insumo.setRutaClaseGenerada(
                rutaClase
        );

        insumo.setRutaXmlGenerada(
                rutaXml
        );

        insumo.setEstado(
                EstadoInsumo.VINCULADO
        );

        return repository.save(
                insumo
        );
    }

    @Transactional
    public InsumoAutomatizacion guardarConRecursos(
            InsumoAutomatizacionForm form,
            String clase,
            String archivoXml) {

        InsumoAutomatizacion insumo =
                guardar(form);

        boolean tieneClase =
                clase != null
                && !clase.isBlank();

        boolean tieneXml =
                archivoXml != null
                && !archivoXml.isBlank();

        if (tieneClase != tieneXml) {

            throw new IllegalArgumentException(
                    "Debe seleccionar la clase Java y el archivo XML."
            );
        }

        if (tieneClase) {

            return vincularRecursos(
                    insumo.getId(),
                    clase,
                    archivoXml
            );
        }

        return insumo;
    }
    public void eliminar(
            Long id) {

        InsumoAutomatizacion insumo =
                buscarPorId(id);

        if (insumo.getEstado()
                == EstadoInsumo.VINCULADO) {

            throw new IllegalArgumentException(
                    "Una clase vinculada no puede eliminarse."
            );
        }

        repository.delete(
                insumo
        );
    }

    private String obligatorio(
            String valor,
            String mensaje) {

        if (valor == null
                || valor.isBlank()) {

            throw new IllegalArgumentException(
                    mensaje
            );
        }

        return valor.trim();
    }

    private String opcional(
            String valor) {

        if (valor == null
                || valor.isBlank()) {

            return null;
        }

        return valor.trim();
    }
}