package com.botengine.framework.utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilidad para generar evidencias visuales de ejecución.
 */
public class ScreenshotUtil {

    private ScreenshotUtil() {
        // Constructor privado para evitar instancias.
    }

    public static String takeScreenshot(WebDriver driver, String scenarioName) {
        String evidencePath = ConfigReader.getProperty("evidence.path");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = scenarioName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";

        File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File destinationFile = new File(evidencePath, fileName);

        try {
            FileUtils.copyFile(sourceFile, destinationFile);
        } catch (IOException exception) {
            throw new RuntimeException("No fue posible guardar la evidencia: " + destinationFile.getPath(), exception);
        }

        return destinationFile.getPath();
    }
}
