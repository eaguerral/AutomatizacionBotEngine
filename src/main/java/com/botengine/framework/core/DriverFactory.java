package com.botengine.framework.core;

import com.botengine.framework.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Fabrica centralizada de navegadores.
 *
 * Permite utilizar Selenium Docker con grabacion real
 * de video o Chrome local como alternativa.
 */
public final class DriverFactory {

    private static final String REMOTE_URL =
            "http://localhost:4444";

    private DriverFactory() {
    }

    public static WebDriver createDriver() {

        String modo =
                System.getProperty(
                        "botengine.selenium.modo",
                        "remote"
                );

        if ("local".equalsIgnoreCase(
                modo
        )) {

            return crearLocal();
        }

        return crearRemoto();
    }

    private static WebDriver crearRemoto() {

        ChromeOptions options =
                new ChromeOptions();

        options.addArguments(
                "--start-maximized"
        );

        options.addArguments(
                "--disable-notifications"
        );

        options.setCapability(
                "se:recordVideo",
                true
        );

        options.setCapability(
                "se:screenResolution",
                "1920x1080"
        );

        String nombre =
                EjecucionContexto.nombreVideo();

        if (nombre == null
                || nombre.isBlank()) {

            nombre =
                    "BotEngine_Ejecucion";
        }

        options.setCapability(
                "se:name",
                nombre
        );

        try {

            return new RemoteWebDriver(
                    new URL(
                            REMOTE_URL
                    ),
                    options
            );

        } catch (MalformedURLException exception) {

            throw new IllegalStateException(
                    "La URL de Selenium remoto no es valida.",
                    exception
            );
        }
    }

    private static WebDriver crearLocal() {

        String browser =
                ConfigReader.getProperty(
                        "browser"
                );

        boolean headless =
                Boolean.parseBoolean(
                        ConfigReader.getProperty(
                                "headless"
                        )
                );

        if (browser == null
                || browser.equalsIgnoreCase(
                        "chrome"
                )) {

            WebDriverManager
                    .chromedriver()
                    .setup();

            ChromeOptions options =
                    new ChromeOptions();

            options.addArguments(
                    "--start-maximized"
            );

            options.addArguments(
                    "--disable-notifications"
            );

            if (headless) {

                options.addArguments(
                        "--headless=new"
                );

                options.addArguments(
                        "--window-size=1920,1080"
                );
            }

            return new ChromeDriver(
                    options
            );
        }

        throw new IllegalArgumentException(
                "Navegador no soportado: "
                + browser
        );
    }
}