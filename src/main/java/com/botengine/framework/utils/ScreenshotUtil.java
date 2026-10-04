package com.botengine.framework.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilidad para generar evidencias visuales de las ejecuciones.
 */
public final class ScreenshotUtil {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern(
                    "yyyyMMdd_HHmmss"
            );

    private ScreenshotUtil() {
    }

    /**
     * Mantiene compatibilidad con ejecuciones manuales
     * que todavia utilizan evidence.path.
     */
    public static String takeScreenshot(
            WebDriver driver,
            String scenarioName) {

        String evidencePath =
                ConfigReader.getProperty(
                        "evidence.path"
                );

        return takeScreenshot(
                driver,
                scenarioName,
                evidencePath
        );
    }

    /**
     * Guarda la captura en la carpeta especifica de una
     * transaccion del portal.
     */
    public static String takeScreenshot(
            WebDriver driver,
            String scenarioName,
            String evidencePath) {

        if (driver == null) {

            throw new IllegalArgumentException(
                    "El WebDriver no puede ser nulo."
            );
        }

        if (evidencePath == null
                || evidencePath.isBlank()) {

            throw new IllegalArgumentException(
                    "La ruta de evidencias no puede estar vacia."
            );
        }

        String timestamp =
                LocalDateTime
                        .now()
                        .format(
                                FORMATO_FECHA
                        );

        String nombreEscenario =
                scenarioName == null
                        || scenarioName.isBlank()
                        ? "evidencia"
                        : scenarioName;

        String fileName =
                nombreEscenario
                        .replaceAll(
                                "[^a-zA-Z0-9_-]",
                                "_"
                        )
                + "_"
                + timestamp
                + ".png";

        Path carpeta =
                Path.of(
                        evidencePath
                )
                .toAbsolutePath()
                .normalize();

        Path destino =
                carpeta.resolve(
                        fileName
                );

        Path temporal =
                ((TakesScreenshot) driver)
                        .getScreenshotAs(
                                OutputType.FILE
                        )
                        .toPath();

        try {

            Files.createDirectories(
                    carpeta
            );

            Files.copy(
                    temporal,
                    destino,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return destino
                    .toString();

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "No fue posible guardar la evidencia: "
                    + destino,
                    exception
            );
        }
    }
}