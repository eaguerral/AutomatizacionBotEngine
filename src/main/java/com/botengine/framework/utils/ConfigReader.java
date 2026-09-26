package com.botengine.framework.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

 private static final String DEFAULT_CONFIG_PATH = "config/config.properties";
 private static final Properties PROPERTIES = new Properties();

 static {
 String configPath = System.getProperty("botengine.config");

 if (configPath == null || configPath.isBlank()) {
 configPath = System.getenv("BOTENGINE_CONFIG");
 }

 if (configPath == null || configPath.isBlank()) {
 configPath = DEFAULT_CONFIG_PATH;
 }

 try (FileInputStream input = new FileInputStream(configPath)) {
 PROPERTIES.load(input);
 } catch (IOException exception) {
 throw new RuntimeException(
 "No fue posible cargar la configuracion: " + configPath +
 ". Cree config/config.properties a partir de config/config.example.properties.",
 exception
 );
 }
 }

 private ConfigReader() {
 }

 public static String getProperty(String key) {
 String value = PROPERTIES.getProperty(key);

 if (value == null || value.trim().isEmpty()) {
 throw new IllegalArgumentException("No existe valor configurado para la propiedad: " + key);
 }

 return value.trim();
 }
}
