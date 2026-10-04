package com.botengine.automatizacion.service;

import com.botengine.automatizacion.model.Automatizacion;
import com.botengine.automatizacion.model.Empresa;
import com.botengine.automatizacion.model.ResultadoEjecucion;
import com.botengine.automatizacion.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/**
 * Administra la estructura física de evidencias generadas
 * por cada ejecución del framework.
 *
 * La implementación es multiempresa y no contiene nombres
 * fijos como ZENTRAMED, LOGIN o cualquier aplicación concreta.
 */
@Service
public class EvidenciaEjecucionService {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern(
                    "yyyyMMdd_HHmmss"
            );

    private final Path directorioBase;

    private final EmpresaRepository empresaRepository;

    private final PlantillaEvidenciaService
            plantillaEvidenciaService;

    public EvidenciaEjecucionService(
            EmpresaRepository empresaRepository,
            PlantillaEvidenciaService plantillaEvidenciaService,
            @Value(
                    "${botengine.evidencias.directorio:"
                    + "storage/evidencias}"
            )
            String directorioBase) {

        this.empresaRepository =
                empresaRepository;

        this.plantillaEvidenciaService =
                plantillaEvidenciaService;

        this.directorioBase =
                Path.of(
                        directorioBase
                )
                .toAbsolutePath()
                .normalize();
    }

    /**
     * Prepara la carpeta y el reporte inicial correspondiente
     * a una ejecución concreta.
     *
     * Ejemplo:
     *
     * storage/evidencias/ZENTRAMED/<uuid>/
     *   Reporte_LOGIN_Zentra_MED_<uuid>_<fecha>.docx
     *   capturas/
     */
    public ResultadoEjecucion preparar(
            ResultadoEjecucion resultado) {

        validarResultado(
                resultado
        );

        Automatizacion automatizacion =
                resultado.getAutomatizacion();

        String sitioCodigo =
                valorOAlternativa(
                        automatizacion.getSitioCodigo(),
                        "GENERAL"
                );

        String empresaNombre =
                obtenerNombreEmpresa(
                        sitioCodigo
                );

        String transaccion =
                obtenerTransaccion(
                        automatizacion,
                        sitioCodigo
                );

        String uuidCorto =
                resultado
                        .getUuid()
                        .replace(
                                "-",
                                ""
                        )
                        .substring(
                                0,
                                8
                        );

        LocalDateTime fecha =
                resultado.getFechaCreacion() != null
                        ? resultado.getFechaCreacion()
                        : LocalDateTime.now();

        String nombreBase =
                "Reporte_"
                + limpiar(
                        transaccion
                )
                + "_"
                + limpiar(
                        empresaNombre
                )
                + "_"
                + uuidCorto
                + "_"
                + fecha.format(
                        FORMATO_FECHA
                );

        Path carpetaEjecucion =
                directorioBase
                        .resolve(
                                limpiar(
                                        sitioCodigo
                                )
                        )
                        .resolve(
                                resultado.getUuid()
                        );

        Path carpetaCapturas =
                carpetaEjecucion.resolve(
                        "capturas"
                );

        try {

            Files.createDirectories(
                    carpetaCapturas
            );

            resultado.setNombreBaseReporte(
                    nombreBase
            );

            resultado.setRutaCapturas(
                    rutaPortable(
                            carpetaCapturas
                    )
            );

            /*
             * Si existe una plantilla activa, se copia a la
             * carpeta específica de la ejecución utilizando
             * el nombre dinámico correspondiente.
             */
            Optional<Path> plantilla =
                    plantillaEvidenciaService
                            .obtenerPlantillaActiva();

            if (plantilla.isPresent()) {

                Path origen =
                        plantilla.get();

                String nombrePlantilla =
                        origen
                                .getFileName()
                                .toString();

                String extension =
                        obtenerExtension(
                                nombrePlantilla
                        );

                Path destino =
                        carpetaEjecucion.resolve(
                                nombreBase
                                + "."
                                + extension
                        );

                Files.copy(
                        origen,
                        destino,
                        StandardCopyOption.REPLACE_EXISTING
                );

                if ("docx".equals(extension)) {

                    resultado.setRutaReporteWord(
                            rutaPortable(
                                    destino
                            )
                    );

                } else if ("pdf".equals(extension)) {

                    resultado.setRutaReportePdf(
                            rutaPortable(
                                    destino
                            )
                    );
                }
            }

            return resultado;

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "No fue posible preparar las evidencias de la ejecución.",
                    exception
            );
        }
    }

    /**
     * Obtiene la transacción de forma dinámica.
     *
     * ZENTRAMED_LOGIN      -> LOGIN
     * MPS_CREAR_FACTURA    -> CREAR_FACTURA
     * EMPRESAX_CONSULTA    -> CONSULTA
     */
    private String obtenerTransaccion(
            Automatizacion automatizacion,
            String sitioCodigo) {

        String codigo =
                valorOAlternativa(
                        automatizacion.getCodigo(),
                        automatizacion.getNombre()
                )
                .trim()
                .toUpperCase(
                        Locale.ROOT
                );

        String sitio =
                sitioCodigo
                        .trim()
                        .toUpperCase(
                                Locale.ROOT
                        );

        String prefijo =
                sitio
                + "_";

        if (codigo.startsWith(prefijo)
                && codigo.length()
                > prefijo.length()) {

            return codigo.substring(
                    prefijo.length()
            );
        }

        return codigo;
    }

    /**
     * Obtiene el nombre visible de la empresa.
     *
     * Si por alguna razón el código todavía no está registrado
     * en empresas, utiliza el código de sitio como respaldo.
     */
    private String obtenerNombreEmpresa(
            String sitioCodigo) {

        return empresaRepository
                .findByCodigo(
                        sitioCodigo
                )
                .map(
                        Empresa::getNombre
                )
                .orElse(
                        sitioCodigo
                );
    }

    private void validarResultado(
            ResultadoEjecucion resultado) {

        if (resultado == null) {

            throw new IllegalArgumentException(
                    "La ejecución no puede ser nula."
            );
        }

        if (resultado.getUuid() == null
                || resultado.getUuid().isBlank()) {

            throw new IllegalStateException(
                    "La ejecución todavía no tiene UUID."
            );
        }

        if (resultado.getAutomatizacion() == null) {

            throw new IllegalStateException(
                    "La ejecución no tiene una automatización asociada."
            );
        }
    }

    /**
     * Limpia textos utilizados como nombres de archivo.
     *
     * Ejemplo:
     * "Zentra MED" -> "Zentra_MED"
     */
    private String limpiar(
            String valor) {

        String normalizado =
                Normalizer.normalize(
                        valor,
                        Normalizer.Form.NFD
                )
                .replaceAll(
                        "\\p{M}",
                        ""
                );

        String limpio =
                normalizado
                        .replaceAll(
                                "[^A-Za-z0-9]+",
                                "_"
                        )
                        .replaceAll(
                                "^_+|_+$",
                                ""
                        );

        return limpio.isBlank()
                ? "SIN_DATO"
                : limpio;
    }

    private String obtenerExtension(
            String nombre) {

        int posicion =
                nombre.lastIndexOf(
                        '.'
                );

        if (posicion < 0
                || posicion
                == nombre.length() - 1) {

            throw new IllegalArgumentException(
                    "La plantilla activa no tiene una extensión válida."
            );
        }

        return nombre
                .substring(
                        posicion + 1
                )
                .toLowerCase(
                        Locale.ROOT
                );
    }

    private String valorOAlternativa(
            String valor,
            String alternativa) {

        if (valor == null
                || valor.isBlank()) {

            return alternativa;
        }

        return valor;
    }

    private String rutaPortable(
            Path ruta) {

        Path absoluta =
                ruta
                        .toAbsolutePath()
                        .normalize();

        Path proyecto =
                Path.of("")
                        .toAbsolutePath()
                        .normalize();

        try {

            return proyecto
                    .relativize(
                            absoluta
                    )
                    .toString()
                    .replace(
                            "\\",
                            "/"
                    );

        } catch (IllegalArgumentException exception) {

            return absoluta
                    .toString()
                    .replace(
                            "\\",
                            "/"
                    );
        }
    }
}