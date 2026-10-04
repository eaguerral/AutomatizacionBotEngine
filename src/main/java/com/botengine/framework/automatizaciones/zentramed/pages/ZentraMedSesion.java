package com.botengine.framework.automatizaciones.zentramed.pages;

import com.botengine.automatizacion.dto.CredencialSitio;
import com.botengine.automatizacion.service.CredencialSitioService;
import com.botengine.framework.core.SpringContext;
import com.botengine.framework.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Precondicion reutilizable para automatizaciones de Zentra MED.
 *
 * Realiza la autenticacion necesaria sin registrar esos pasos
 * como parte del reporte funcional de otro requerimiento.
 */
public final class ZentraMedSesion {

    private ZentraMedSesion() {
    }

    public static void asegurarSesion(
            WebDriver driver) {

        CredencialSitioService servicio =
                SpringContext.getBean(
                        CredencialSitioService.class
                );

        CredencialSitio credencial =
                servicio
                        .obtenerCredencialDisponible(
                                "ZENTRAMED"
                        );

        Duration timeout =
                Duration.ofSeconds(
                        Math.max(
                                Integer.parseInt(
                                        ConfigReader.getProperty(
                                                "timeout.seconds"
                                        )
                                ),
                                30
                        )
                );

        driver.get(
                credencial.url()
        );

        ZentraMedLogin login =
                new ZentraMedLogin(
                        driver
                );

        login.esperarFormulario(
                timeout
        );

        login.iniciarSesion(
                credencial.username(),
                credencial.password()
        );

        WebDriverWait wait =
                new WebDriverWait(
                        driver,
                        timeout
                );

        wait.until(
                webDriver ->
                        login.loginExitoso()
                        || login.errorVisible()
        );

        if (login.errorVisible()) {

            throw new IllegalStateException(
                    "No fue posible iniciar sesion en Zentra MED."
            );
        }

        wait.until(
                webDriver ->
                        login.loginExitoso()
        );
    }
}