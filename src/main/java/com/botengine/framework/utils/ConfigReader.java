package com.botengine.framework.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static final String DEFAULT_CONFIG_PATH =
            "config/config.properties";

    private static final Properties PROPERTIES =
            new Properties();

    static {

        String configPath =
                System.getProperty(
                        "botengine.config"
                );

        if (configPath == null
                || configPath.isBlank()) {

            configPath =
                    System.getenv(
                            "BOTENGINE_CONFIG"
                    );
        }

        if (configPath == null
                || configPath.isBlank()) {

            configPath =
                    DEFAULT_CONFIG_PATH;
        }

        try (FileInputStream input =
                     new FileInputStream(
                             configPath
                     )) {

            PROPERTIES.load(input);

        } catch (IOException exception) {

            System.out.println(
                    "Advertencia: no se cargo "
                    + configPath
                    + ". Se utilizaran variables de entorno cuando existan."
            );
        }
    }

    private ConfigReader() {
    }

    public static String getProperty(
            String key) {

        String systemValue =
                System.getProperty(key);

        if (tieneValor(systemValue)) {
            return systemValue.trim();
        }

        String envKey =
                convertirAEnv(key);

        String envValue =
                System.getenv(envKey);

        if (tieneValor(envValue)) {
            return envValue.trim();
        }

        String propertyValue =
                PROPERTIES.getProperty(key);

        if (tieneValor(propertyValue)) {
            return propertyValue.trim();
        }

        throw new IllegalArgumentException(
                "No existe valor configurado para la propiedad: "
                + key
                + " ni para la variable de entorno: "
                + envKey
        );
    }

    public static String getEnv(
            String envKey) {

        String value =
                System.getenv(envKey);

        if (!tieneValor(value)) {

            throw new IllegalArgumentException(
                    "No existe la variable de entorno: "
                    + envKey
            );
        }

        return value.trim();
    }

    public static String getEnvOptional(
            String envKey) {

        String value =
                System.getenv(envKey);

        if (!tieneValor(value)) {
            return null;
        }

        return value.trim();
    }

    private static String convertirAEnv(
            String key) {

        return key
                .replace(".", "_")
                .replace("-", "_")
                .toUpperCase();
    }

    private static boolean tieneValor(
            String value) {

        return value != null
                && !value.isBlank();
    }
}