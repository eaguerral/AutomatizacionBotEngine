package com.botengine.framework.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Lector de propiedades del framework.
 */
public class ConfigReader {

    private static final String CONFIG_PATH = "src/test/resources/config/config.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        try (FileInputStream input = new FileInputStream(CONFIG_PATH)) {
            PROPERTIES.load(input);
        } catch (IOException exception) {
            throw new RuntimeException("No fue posible cargar el archivo de configuración: " + CONFIG_PATH, exception);
        }
    }

    private ConfigReader() {
        // Constructor privado para evitar instancias.
    }

    public static String getProperty(String key) {
        String value = PROPERTIES.getProperty(key);

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("No existe valor configurado para la propiedad: " + key);
        }

        return value.trim();
    }
}
