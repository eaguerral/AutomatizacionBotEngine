package com.botengine.automatizacion.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class RecursoAutomatizacionDetectorService {

    private static final String PAQUETE_BASE =
            "com.botengine.framework.automatizaciones";

    private static final String RUTA_CLASES_BASE =
            "com/botengine/framework/automatizaciones";

    private static final String RUTA_XML_BASE =
            "automatizaciones/xml";

    private final PathMatchingResourcePatternResolver resolver =
            new PathMatchingResourcePatternResolver();

    public List<String> detectarClases(
            String carpetaEmpresa) {

        String carpeta =
                normalizarCarpeta(carpetaEmpresa);

        String patron =
                "classpath*:"
                + RUTA_CLASES_BASE
                + "/"
                + carpeta
                + "/tests/*.class";

        return obtenerNombres(
                patron,
                ".class",
                true
        );
    }

    public List<String> detectarXml(
            String carpetaEmpresa) {

        String carpeta =
                normalizarCarpeta(carpetaEmpresa);

        String patron =
                "classpath*:"
                + RUTA_XML_BASE
                + "/"
                + carpeta
                + "/*.xml";

        return obtenerNombresXml(
                patron
        );
    }

    public String generarRutaClase(
            String carpetaEmpresa,
            String clase) {

        String carpeta =
                normalizarCarpeta(carpetaEmpresa);

        String nombreClase =
                validarNombreClase(clase);

        return PAQUETE_BASE
                + "."
                + carpeta
                + ".tests."
                + nombreClase;
    }

    public String generarRutaXml(
            String carpetaEmpresa,
            String archivoXml) {

        String carpeta =
                normalizarCarpeta(carpetaEmpresa);

        String xml =
                validarNombreXml(archivoXml);

        return RUTA_XML_BASE
                + "/"
                + carpeta
                + "/"
                + xml;
    }

    public void validarClaseDetectada(
            String carpetaEmpresa,
            String clase) {

        String nombreClase =
                validarNombreClase(clase);

        if (!detectarClases(carpetaEmpresa)
                .contains(nombreClase)) {

            throw new IllegalArgumentException(
                    "La clase seleccionada no fue detectada en el framework."
            );
        }
    }

    public void validarXmlDetectado(
            String carpetaEmpresa,
            String archivoXml) {

        String xml =
                validarNombreXml(archivoXml);

        if (!detectarXml(carpetaEmpresa)
                .contains(xml)) {

            throw new IllegalArgumentException(
                    "El archivo XML seleccionado no fue detectado en el framework."
            );
        }
    }

    private List<String> obtenerNombresXml(
            String patron) {

        try {

            Resource[] recursos =
                    resolver.getResources(
                            patron
                    );

            return Arrays.stream(recursos)
                    .map(Resource::getFilename)
                    .filter(Objects::nonNull)
                    .filter(nombre ->
                            nombre.endsWith(".xml")
                    )
                    .distinct()
                    .sorted()
                    .toList();

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "No fue posible analizar los archivos XML del framework.",
                    exception
            );
        }
    }
    private List<String> obtenerNombres(
            String patron,
            String extension,
            boolean ignorarClasesInternas) {

        try {

            Resource[] recursos =
                    resolver.getResources(
                            patron
                    );

            return Arrays.stream(recursos)
                    .map(Resource::getFilename)
                    .filter(Objects::nonNull)
                    .filter(nombre ->
                            nombre.endsWith(
                                    extension
                            )
                    )
                    .filter(nombre ->
                            !ignorarClasesInternas
                            || !nombre.contains("$")
                    )
                    .map(nombre ->
                            nombre.substring(
                                    0,
                                    nombre.length()
                                    - extension.length()
                            )
                    )
                    .distinct()
                    .sorted()
                    .toList();

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "No fue posible analizar los recursos del framework.",
                    exception
            );
        }
    }

    private String normalizarCarpeta(
            String carpeta) {

        if (carpeta == null
                || carpeta.isBlank()) {

            throw new IllegalArgumentException(
                    "La carpeta de la empresa es obligatoria."
            );
        }

        String valor =
                carpeta.trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

        if (!valor.matches(
                "[a-z0-9_]+"
        )) {

            throw new IllegalArgumentException(
                    "La carpeta de empresa contiene caracteres no permitidos."
            );
        }

        return valor;
    }

    private String validarNombreClase(
            String clase) {

        if (clase == null
                || clase.isBlank()) {

            throw new IllegalArgumentException(
                    "Debe seleccionar una clase Java."
            );
        }

        String valor =
                clase.trim();

        if (!valor.matches(
                "[A-Za-z_$][A-Za-z0-9_$]*"
        )) {

            throw new IllegalArgumentException(
                    "El nombre de la clase Java no es válido."
            );
        }

        return valor;
    }

    private String validarNombreXml(
            String archivoXml) {

        if (archivoXml == null
                || archivoXml.isBlank()) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un archivo XML."
            );
        }

        String valor =
                archivoXml.trim();

        if (!valor.matches(
                "[A-Za-z0-9._-]+\\.xml"
        )) {

            throw new IllegalArgumentException(
                    "El nombre del archivo XML no es válido."
            );
        }

        return valor;
    }
}