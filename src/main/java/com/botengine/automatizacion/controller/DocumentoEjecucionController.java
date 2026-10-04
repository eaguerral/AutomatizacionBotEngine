package com.botengine.automatizacion.controller;

import com.botengine.automatizacion.model.ResultadoEjecucion;
import com.botengine.automatizacion.repository.ResultadoEjecucionRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
public class DocumentoEjecucionController {

    private final ResultadoEjecucionRepository resultadoRepository;

    public DocumentoEjecucionController(
            ResultadoEjecucionRepository resultadoRepository) {

        this.resultadoRepository =
                resultadoRepository;
    }


    // ========================================================
    // PDF
    // ========================================================

    @GetMapping(
            "/documentacion/ejecuciones/{uuid}/pdf"
    )
    public ResponseEntity<Resource> verPdf(
            @PathVariable
            String uuid) {

        ResultadoEjecucion resultado =
                buscar(
                        uuid
                );

        return servir(
                resultado.getRutaReportePdf(),
                MediaType.APPLICATION_PDF,
                false
        );
    }


    @GetMapping(
            "/documentacion/ejecuciones/{uuid}/pdf/descargar"
    )
    public ResponseEntity<Resource> descargarPdf(
            @PathVariable
            String uuid) {

        ResultadoEjecucion resultado =
                buscar(
                        uuid
                );

        return servir(
                resultado.getRutaReportePdf(),
                MediaType.APPLICATION_PDF,
                true
        );
    }


    // ========================================================
    // VIDEO
    // ========================================================

    @GetMapping(
            "/documentacion/ejecuciones/{uuid}/video"
    )
    public ResponseEntity<Resource> verVideo(
            @PathVariable
            String uuid) {

        ResultadoEjecucion resultado =
                buscar(
                        uuid
                );

        return servir(
                resultado.getRutaVideo(),
                MediaType.parseMediaType(
                        "video/mp4"
                ),
                false
        );
    }


    @GetMapping(
            "/documentacion/ejecuciones/{uuid}/video/descargar"
    )
    public ResponseEntity<Resource> descargarVideo(
            @PathVariable
            String uuid) {

        ResultadoEjecucion resultado =
                buscar(
                        uuid
                );

        return servir(
                resultado.getRutaVideo(),
                MediaType.parseMediaType(
                        "video/mp4"
                ),
                true
        );
    }


    // ========================================================
    // WORD
    // ========================================================

    @GetMapping(
            "/documentacion/ejecuciones/{uuid}/word"
    )
    public ResponseEntity<Resource> descargarWord(
            @PathVariable
            String uuid) {

        ResultadoEjecucion resultado =
                buscar(
                        uuid
                );

        return servir(
                resultado.getRutaReporteWord(),
                MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                ),
                true
        );
    }


    private ResultadoEjecucion buscar(
            String uuid) {

        return resultadoRepository
                .findByUuid(
                        uuid
                )
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "La ejecucion no existe."
                                )
                );
    }


    private ResponseEntity<Resource> servir(
            String ruta,
            MediaType mediaType,
            boolean descarga) {

        try {

            if (ruta == null
                    || ruta.isBlank()) {

                return ResponseEntity
                        .notFound()
                        .build();
            }

            Path archivo =
                    Path.of(
                            ruta
                    )
                    .toAbsolutePath()
                    .normalize();

            if (!Files.exists(
                    archivo
            )
                    || !Files.isRegularFile(
                            archivo
                    )) {

                return ResponseEntity
                        .notFound()
                        .build();
            }

            Resource recurso =
                    new UrlResource(
                            archivo.toUri()
                    );

            ContentDisposition disposition;

            if (descarga) {

                disposition =
                        ContentDisposition
                                .attachment()
                                .filename(
                                        archivo
                                                .getFileName()
                                                .toString(),
                                        StandardCharsets.UTF_8
                                )
                                .build();

            } else {

                disposition =
                        ContentDisposition
                                .inline()
                                .filename(
                                        archivo
                                                .getFileName()
                                                .toString(),
                                        StandardCharsets.UTF_8
                                )
                                .build();
            }

            return ResponseEntity
                    .ok()
                    .contentType(
                            mediaType
                    )
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            disposition.toString()
                    )
                    .header(
                            HttpHeaders.CACHE_CONTROL,
                            "no-store"
                    )
                    .body(
                            recurso
                    );

        } catch (Exception exception) {

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }
}