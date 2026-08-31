package com.botengine.framework.core;

import com.botengine.framework.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

/**
 * Clase base para las pruebas funcionales automatizadas.
 * Define la preparación y cierre del navegador.
 */
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = DriverFactory.createDriver();

        int timeoutSeconds = Integer.parseInt(ConfigReader.getProperty("timeout.seconds"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeoutSeconds));

        String baseUrl = ConfigReader.getProperty("base.url");
        driver.get(baseUrl);
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
