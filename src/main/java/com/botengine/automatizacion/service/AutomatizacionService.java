package com.botengine.automatizacion.service;

import com.botengine.automatizacion.dto.AutomatizacionForm;
import com.botengine.automatizacion.model.Automatizacion;
import com.botengine.automatizacion.repository.AutomatizacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class AutomatizacionService {

    private static final String PAQUETE_TESTS_LEGACY =
            "com.botengine.framework.tests.";

    private static final String PAQUETE_AUTOMATIZACIONES =
            "com.botengine.framework.automatizaciones.";

    private static final String RUTA_XML_BASE =
            "automatizaciones/xml/";

    private final AutomatizacionRepository automatizacionRepository;

    public AutomatizacionService(
            AutomatizacionRepository automatizacionRepository) {

        this.automatizacionRepository =
                automatizacionRepository;
    }

    public List<Automatizacion> listarTodas() {
        return automatizacionRepository.findAll();
    }

    public List<Automatizacion> listarActivas() {
        return automatizacionRepository
                .findByActivoTrueOrderByNombreAsc();
    }

    public Automatizacion buscarPorId(Long id) {

        return automatizacionRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La automatizacion no existe."
                        )
                );
    }

    public Automatizacion crear(
            AutomatizacionForm form) {

        String codigo =
                normalizarCodigo(
                        form.getCodigo()
                );

        String nombre =
                obligatorio(
                        form.getNombre(),
                        "El nombre es obligatorio."
                );

        String claseTest =
                obligatorio(
                        form.getClaseTest(),
                        "La clase TestNG es obligatoria."
                );

        String sitioCodigo =
                normalizarSitioCodigo(
                        form.getSitioCodigo()
                );

        String archivoXml =
                normalizarOpcional(
                        form.getArchivoXml()
                );

        validarClaseTest(
                claseTest
        );

        validarArchivoXml(
                archivoXml
        );

        if (automatizacionRepository
                .existsByCodigo(codigo)) {

            throw new IllegalArgumentException(
                    "El codigo de automatizacion ya existe."
            );
        }

        if (automatizacionRepository
                .existsByClaseTest(claseTest)) {

            throw new IllegalArgumentException(
                    "La clase TestNG ya se encuentra registrada."
            );
        }

        Automatizacion automatizacion =
                new Automatizacion();

        automatizacion.setCodigo(
                codigo
        );

        automatizacion.setNombre(
                nombre
        );

        automatizacion.setDescripcion(
                normalizarOpcional(
                        form.getDescripcion()
                )
        );

        automatizacion.setClaseTest(
                claseTest
        );

        automatizacion.setSitioCodigo(
                sitioCodigo
        );

        automatizacion.setArchivoXml(
                archivoXml
        );

        automatizacion.setActivo(
                form.isActivo()
        );

        return automatizacionRepository
                .save(
                        automatizacion
                );
    }

    public AutomatizacionForm
    obtenerFormularioEdicion(Long id) {

        Automatizacion automatizacion =
                buscarPorId(id);

        AutomatizacionForm form =
                new AutomatizacionForm();

        form.setId(
                automatizacion.getId()
        );

        form.setCodigo(
                automatizacion.getCodigo()
        );

        form.setNombre(
                automatizacion.getNombre()
        );

        form.setDescripcion(
                automatizacion.getDescripcion()
        );

        form.setClaseTest(
                automatizacion.getClaseTest()
        );

        form.setSitioCodigo(
                automatizacion.getSitioCodigo()
        );

        form.setArchivoXml(
                automatizacion.getArchivoXml()
        );

        form.setActivo(
                automatizacion.isActivo()
        );

        return form;
    }

    public Automatizacion actualizar(
            Long id,
            AutomatizacionForm form) {

        Automatizacion automatizacion =
                buscarPorId(id);

        String codigo =
                normalizarCodigo(
                        form.getCodigo()
                );

        String nombre =
                obligatorio(
                        form.getNombre(),
                        "El nombre es obligatorio."
                );

        String claseTest =
                obligatorio(
                        form.getClaseTest(),
                        "La clase TestNG es obligatoria."
                );

        String sitioCodigo =
                normalizarSitioCodigo(
                        form.getSitioCodigo()
                );

        String archivoXml =
                normalizarOpcional(
                        form.getArchivoXml()
                );

        validarClaseTest(
                claseTest
        );

        validarArchivoXml(
                archivoXml
        );

        if (automatizacionRepository
                .existsByCodigoAndIdNot(
                        codigo,
                        id
                )) {

            throw new IllegalArgumentException(
                    "El codigo de automatizacion ya existe."
            );
        }

        if (automatizacionRepository
                .existsByClaseTestAndIdNot(
                        claseTest,
                        id
                )) {

            throw new IllegalArgumentException(
                    "La clase TestNG ya se encuentra registrada."
            );
        }

        automatizacion.setCodigo(
                codigo
        );

        automatizacion.setNombre(
                nombre
        );

        automatizacion.setDescripcion(
                normalizarOpcional(
                        form.getDescripcion()
                )
        );

        automatizacion.setClaseTest(
                claseTest
        );

        automatizacion.setSitioCodigo(
                sitioCodigo
        );

        automatizacion.setArchivoXml(
                archivoXml
        );

        automatizacion.setActivo(
                form.isActivo()
        );

        return automatizacionRepository
                .save(
                        automatizacion
                );
    }

    private String normalizarCodigo(
            String codigo) {

        return obligatorio(
                codigo,
                "El codigo es obligatorio."
        )
                .replace(" ", "_")
                .replace("-", "_")
                .toUpperCase(Locale.ROOT);
    }

    private String normalizarSitioCodigo(
            String sitioCodigo) {

        if (sitioCodigo == null
                || sitioCodigo.isBlank()) {

            return null;
        }

        return sitioCodigo
                .trim()
                .replace(" ", "_")
                .replace("-", "_")
                .toUpperCase(Locale.ROOT);
    }

    private void validarClaseTest(
            String claseTest) {

        boolean paqueteLegacy =
                claseTest.startsWith(
                        PAQUETE_TESTS_LEGACY
                );

        boolean paqueteNuevo =
                claseTest.startsWith(
                        PAQUETE_AUTOMATIZACIONES
                );

        if (!paqueteLegacy
                && !paqueteNuevo) {

            throw new IllegalArgumentException(
                    "La clase debe pertenecer a "
                    + PAQUETE_TESTS_LEGACY
                    + " o "
                    + PAQUETE_AUTOMATIZACIONES
            );
        }

        try {

            Class.forName(
                    claseTest
            );

        } catch (ClassNotFoundException exception) {

            throw new IllegalArgumentException(
                    "La clase TestNG indicada no existe."
            );
        }
    }

    private void validarArchivoXml(
            String archivoXml) {

        if (archivoXml == null) {
            return;
        }

        if (!archivoXml.startsWith(
                RUTA_XML_BASE
        )) {

            throw new IllegalArgumentException(
                    "El XML debe encontrarse dentro de "
                    + RUTA_XML_BASE
            );
        }

        if (!archivoXml
                .toLowerCase(Locale.ROOT)
                .endsWith(".xml")) {

            throw new IllegalArgumentException(
                    "El archivo de ejecucion debe tener extension .xml."
            );
        }
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

    private String normalizarOpcional(
            String valor) {

        if (valor == null
                || valor.isBlank()) {

            return null;
        }

        return valor.trim();
    }
}