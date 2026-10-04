package com.botengine.framework.core;

import com.botengine.framework.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

/**
 * Clase base de las pruebas funcionales automatizadas.
 */
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {

        driver =
                DriverFactory.createDriver();

        int timeoutSeconds =
                Integer.parseInt(
                        ConfigReader.getProperty(
                                "timeout.seconds"
                        )
                );

        driver.manage()
                .timeouts()
                .implicitlyWait(
                        Duration.ofSeconds(
                                timeoutSeconds
                        )
                );

        String baseUrl =
                ConfigReader.getProperty(
                        "base.url"
                );

        /*
         * Cuando Chrome corre dentro de Docker,
         * localhost pertenece al contenedor.
         *
         * host.docker.internal permite acceder al
         * portal que se ejecuta en Windows.
         */
        if (driver instanceof RemoteWebDriver) {

            baseUrl =
                    baseUrl
                            .replace(
                                    "://localhost",
                                    "://host.docker.internal"
                            )
                            .replace(
                                    "://127.0.0.1",
                                    "://host.docker.internal"
                            );
        }

        driver.get(
                baseUrl
        );
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        if (driver != null) {

            driver.quit();
        }
    }

    public WebDriver getDriver() {

        return driver;
    }
}