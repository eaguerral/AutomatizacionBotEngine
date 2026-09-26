package com.botengine.framework.core;

import com.botengine.framework.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Fábrica de navegadores del framework.
 * Permite centralizar la creación del WebDriver.
 */
public class DriverFactory {

    private DriverFactory() {
        // Constructor privado para evitar instancias.
    }

    public static WebDriver createDriver() {
        String browser = ConfigReader.getProperty("browser");
        boolean headless = Boolean.parseBoolean(ConfigReader.getProperty("headless"));

        if (browser == null || browser.equalsIgnoreCase("chrome")) {
            WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");

            if (headless) {
                options.addArguments("--headless=new");
                options.addArguments("--window-size=1920,1080");
            }

            return new ChromeDriver(options);
        }

        throw new IllegalArgumentException("Navegador no soportado en esta base inicial: " + browser);
    }
}
