package com.botengine.framework.utils;

import org.openqa.selenium.WebDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Registra los pasos funcionales definidos por cada automatizacion.
 *
 * Los nombres y descripciones pertenecen al requerimiento
 * concreto, no al generador de reportes.
 */
public final class EvidenciaPaso {

    private static final InheritableThreadLocal<Contexto> CONTEXTO =
            new InheritableThreadLocal<>();

    private EvidenciaPaso() {
    }

    public static void iniciar(
            String directorioCapturas) {

        if (directorioCapturas == null
                || directorioCapturas.isBlank()) {

            CONTEXTO.remove();
            return;
        }

        try {

            Path carpeta =
                    Path.of(
                            directorioCapturas
                    )
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(
                    carpeta
            );

            Path manifiesto =
                    carpeta.resolve(
                            "pasos.tsv"
                    );

            Files.deleteIfExists(
                    manifiesto
            );

            CONTEXTO.set(
                    new Contexto(
                            carpeta,
                            manifiesto
                    )
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "No fue posible iniciar el registro de evidencias.",
                    exception
            );
        }
    }

    public static boolean activa() {

        return CONTEXTO.get() != null;
    }

    public static void finalizar() {

        CONTEXTO.remove();
    }

    public static String capturar(
            WebDriver driver,
            String titulo) {

        return capturar(
                driver,
                titulo,
                ""
        );
    }

    public static String capturar(
            WebDriver driver,
            String titulo,
            String descripcion) {

        Contexto contexto =
                CONTEXTO.get();

        if (contexto == null) {

            return ScreenshotUtil.takeScreenshot(
                    driver,
                    limpiar(
                            titulo
                    )
            );
        }

        int numero =
                contexto.siguiente();

        String nombre =
                String.format(
                        "%02d_%s",
                        numero,
                        limpiar(
                                titulo
                        )
                );

        String ruta =
                ScreenshotUtil.takeScreenshot(
                        driver,
                        nombre,
                        contexto
                                .carpeta()
                                .toString()
                );

        registrar(
                contexto,
                numero,
                titulo,
                descripcion,
                Path.of(
                        ruta
                )
                .getFileName()
                .toString()
        );

        return ruta;
    }

    private static synchronized void registrar(
            Contexto contexto,
            int numero,
            String titulo,
            String descripcion,
            String archivo) {

        try {

            String linea =
                    numero
                    + "\t"
                    + sanitizar(
                            titulo
                    )
                    + "\t"
                    + sanitizar(
                            descripcion
                    )
                    + "\t"
                    + archivo
                    + System.lineSeparator();

            Files.writeString(
                    contexto.manifiesto(),
                    linea,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "No fue posible registrar el paso.",
                    exception
            );
        }
    }

    private static String sanitizar(
            String valor) {

        if (valor == null) {
            return "";
        }

        return valor
                .replace(
                        "\t",
                        " "
                )
                .replace(
                        "\r",
                        " "
                )
                .replace(
                        "\n",
                        " "
                );
    }

    private static String limpiar(
            String valor) {

        if (valor == null
                || valor.isBlank()) {

            return "evidencia";
        }

        String limpio =
                valor
                        .replaceAll(
                                "[^A-Za-z0-9]+",
                                "_"
                        )
                        .replaceAll(
                                "^_+|_+$",
                                ""
                        );

        return limpio.isBlank()
                ? "evidencia"
                : limpio;
    }

    private static final class Contexto {

        private final Path carpeta;
        private final Path manifiesto;

        private int contador;

        private Contexto(
                Path carpeta,
                Path manifiesto) {

            this.carpeta =
                    carpeta;

            this.manifiesto =
                    manifiesto;

            this.contador =
                    0;
        }

        private synchronized int siguiente() {

            contador++;

            return contador;
        }

        private Path carpeta() {
            return carpeta;
        }

        private Path manifiesto() {
            return manifiesto;
        }
    }
}