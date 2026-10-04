package com.botengine.automatizacion.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Administra la plantilla oficial utilizada para generar
 * las evidencias de las automatizaciones.
 *
 * Solo puede existir una plantilla activa.
 *
 * Cuando se carga una nueva plantilla:
 * 1. Se valida el formato.
 * 2. Se almacena primero como archivo temporal.
 * 3. Se elimina la plantilla anterior.
 * 4. Se establece la nueva como plantilla activa.
 */
@Service
public class PlantillaEvidenciaService {

    private static final long TAMANO_MAXIMO =
            20L * 1024L * 1024L;

    private static final List<String> EXTENSIONES_PERMITIDAS =
            List.of(
                    "docx",
                    "pdf"
            );

    private final Path directorio;

    public PlantillaEvidenciaService(
            @Value(
                    "${botengine.plantillas.directorio:"
                    + "storage/plantillas-evidencia}"
            )
            String directorio) {

        this.directorio =
                Path.of(
                        directorio
                )
                .toAbsolutePath()
                .normalize();

        inicializarDirectorio();
    }

    /**
     * Guarda una nueva plantilla y reemplaza cualquier
     * plantilla activa existente.
     */
    public synchronized Path guardar(
            MultipartFile archivo) {

        validarArchivo(
                archivo
        );

        String extension =
                obtenerExtension(
                        archivo.getOriginalFilename()
                );

        String nombreDestino =
                "plantilla-evidencias."
                + extension;

        Path temporal =
                directorio.resolve(
                        "plantilla-evidencias.tmp"
                );

        Path destino =
                directorio.resolve(
                        nombreDestino
                );

        try {

            /*
             * Primero se copia el nuevo archivo a una ruta
             * temporal para evitar perder la plantilla anterior
             * si la carga falla.
             */
            Files.copy(
                    archivo.getInputStream(),
                    temporal,
                    StandardCopyOption.REPLACE_EXISTING
            );

            /*
             * Una vez confirmada la carga, se eliminan
             * las plantillas anteriores.
             */
            eliminarPlantillasActivas();

            try {

                Files.move(
                        temporal,
                        destino,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                );

            } catch (
                    AtomicMoveNotSupportedException exception) {

                Files.move(
                        temporal,
                        destino,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            return destino;

        } catch (IOException exception) {

            try {
                Files.deleteIfExists(
                        temporal
                );
            } catch (IOException ignored) {
                // No impedir que se propague el error original.
            }

            throw new IllegalStateException(
                    "No fue posible almacenar la nueva plantilla de evidencias.",
                    exception
            );
        }
    }

    /**
     * Devuelve la única plantilla activa.
     *
     * Este método será utilizado posteriormente por
     * el generador de evidencias de cada automatización.
     */
    public Optional<Path> obtenerPlantillaActiva() {

        try (Stream<Path> archivos =
                     Files.list(
                             directorio
                     )) {

            return archivos
                    .filter(
                            Files::isRegularFile
                    )
                    .filter(
                            this::esPlantillaActiva
                    )
                    .max(
                            Comparator.comparing(
                                    this::obtenerFechaModificacion
                            )
                    );

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "No fue posible consultar la plantilla activa.",
                    exception
            );
        }
    }

    public boolean existePlantillaActiva() {

        return obtenerPlantillaActiva()
                .isPresent();
    }

    public String obtenerNombrePlantillaActiva() {

        return obtenerPlantillaActiva()
                .map(path ->
                        path.getFileName()
                                .toString()
                )
                .orElse(null);
    }

    public String obtenerFormatoPlantillaActiva() {

        return obtenerPlantillaActiva()
                .map(path ->
                        obtenerExtension(
                                path.getFileName()
                                        .toString()
                        )
                                .toUpperCase(
                                        Locale.ROOT
                                )
                )
                .orElse(null);
    }

    public Instant obtenerFechaPlantillaActiva() {

        return obtenerPlantillaActiva()
                .map(
                        this::obtenerFechaModificacion
                )
                .orElse(null);
    }

    private void validarArchivo(
            MultipartFile archivo) {

        if (archivo == null
                || archivo.isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un archivo Word o PDF."
            );
        }

        if (archivo.getSize()
                > TAMANO_MAXIMO) {

            throw new IllegalArgumentException(
                    "La plantilla no puede superar los 20 MB."
            );
        }

        String nombre =
                archivo.getOriginalFilename();

        String extension =
                obtenerExtension(
                        nombre
                );

        if (!EXTENSIONES_PERMITIDAS
                .contains(
                        extension
                )) {

            throw new IllegalArgumentException(
                    "Solo se permiten plantillas .docx o .pdf."
            );
        }
    }

    private String obtenerExtension(
            String nombre) {

        if (nombre == null
                || nombre.isBlank()
                || !nombre.contains(".")) {

            throw new IllegalArgumentException(
                    "El archivo seleccionado no tiene una extensión válida."
            );
        }

        return nombre
                .substring(
                        nombre.lastIndexOf('.') + 1
                )
                .trim()
                .toLowerCase(
                        Locale.ROOT
                );
    }

    private void eliminarPlantillasActivas()
            throws IOException {

        try (Stream<Path> archivos =
                     Files.list(
                             directorio
                     )) {

            archivos
                    .filter(
                            Files::isRegularFile
                    )
                    .filter(
                            this::esPlantillaActiva
                    )
                    .forEach(path -> {

                        try {

                            Files.deleteIfExists(
                                    path
                            );

                        } catch (IOException exception) {

                            throw new IllegalStateException(
                                    "No fue posible reemplazar la plantilla anterior.",
                                    exception
                            );
                        }
                    });
        }
    }

    private boolean esPlantillaActiva(
            Path path) {

        String nombre =
                path.getFileName()
                        .toString()
                        .toLowerCase(
                                Locale.ROOT
                        );

        return nombre.equals(
                "plantilla-evidencias.docx"
        )
                || nombre.equals(
                "plantilla-evidencias.pdf"
        );
    }

    private Instant obtenerFechaModificacion(
            Path path) {

        try {

            return Files
                    .getLastModifiedTime(
                            path
                    )
                    .toInstant();

        } catch (IOException exception) {

            return Instant.EPOCH;
        }
    }

    private void inicializarDirectorio() {

        try {

            Files.createDirectories(
                    directorio
            );

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "No fue posible crear el directorio de plantillas.",
                    exception
            );
        }
    }
}