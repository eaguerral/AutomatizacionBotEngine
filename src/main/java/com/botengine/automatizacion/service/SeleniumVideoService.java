package com.botengine.automatizacion.service;

import com.botengine.automatizacion.model.ResultadoEjecucion;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;

/**
 * Recupera el MP4 real generado por Selenium Docker.
 */
@Service
public class SeleniumVideoService {

    private static final Path DIRECTORIO_SELENIUM =
            Path.of(
                    "storage",
                    "selenium-videos"
            )
            .toAbsolutePath()
            .normalize();

    private static final Duration TIEMPO_ESPERA =
            Duration.ofSeconds(
                    45
            );

    public void asociar(
            ResultadoEjecucion resultado) {

        if (resultado == null
                || resultado.getNombreBaseReporte() == null
                || resultado.getRutaCapturas() == null) {

            return;
        }

        String nombreVideo =
                limpiarNombre(
                        resultado.getNombreBaseReporte()
                )
                + ".mp4";

        Path origen =
                DIRECTORIO_SELENIUM.resolve(
                        nombreVideo
                );

        esperarVideo(
                origen
        );

        if (!Files.exists(
                origen
        )) {

            throw new IllegalStateException(
                    "Selenium no genero el video esperado: "
                    + origen
            );
        }

        Path carpetaEjecucion =
                Path.of(
                        resultado.getRutaCapturas()
                )
                .toAbsolutePath()
                .normalize()
                .getParent();

        Path destino =
                carpetaEjecucion.resolve(
                        nombreVideo
                );

        try {

            Files.copy(
                    origen,
                    destino,
                    StandardCopyOption.REPLACE_EXISTING
            );

            resultado.setRutaVideo(
                    rutaPortable(
                            destino
                    )
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "No fue posible asociar el video Selenium.",
                    exception
            );
        }
    }

    private void esperarVideo(
            Path archivo) {

        Instant limite =
                Instant.now()
                        .plus(
                                TIEMPO_ESPERA
                        );

        long tamanoAnterior =
                -1L;

        int comprobacionesEstables =
                0;

        while (Instant.now()
                .isBefore(
                        limite
                )) {

            try {

                if (Files.exists(
                        archivo
                )) {

                    long tamano =
                            Files.size(
                                    archivo
                            );

                    if (tamano > 0
                            && tamano == tamanoAnterior) {

                        comprobacionesEstables++;

                        if (comprobacionesEstables >= 2) {

                            return;
                        }

                    } else {

                        comprobacionesEstables =
                                0;
                    }

                    tamanoAnterior =
                            tamano;
                }

                Thread.sleep(
                        1500L
                );

            } catch (InterruptedException exception) {

                Thread.currentThread()
                        .interrupt();

                return;

            } catch (Exception ignored) {
            }
        }
    }

    private String limpiarNombre(
            String nombre) {

        return nombre
                .replaceAll(
                        "[^a-zA-Z0-9_-]",
                        "_"
                );
    }

    private String rutaPortable(
            Path ruta) {

        Path proyecto =
                Path.of("")
                        .toAbsolutePath()
                        .normalize();

        try {

            return proyecto
                    .relativize(
                            ruta
                    )
                    .toString()
                    .replace(
                            "\\",
                            "/"
                    );

        } catch (Exception exception) {

            return ruta
                    .toString()
                    .replace(
                            "\\",
                            "/"
                    );
        }
    }
}