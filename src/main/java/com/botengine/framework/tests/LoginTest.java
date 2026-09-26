package com.botengine.framework.tests;

import com.botengine.framework.core.BaseTest;
import com.botengine.framework.pages.LoginPage;
import com.botengine.framework.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(description = "Validar inicio de sesion del portal AutomatizacionBotEngine")
    public void validarLoginBase() {

        LoginPage loginPage = new LoginPage(driver);

        String usuario = ConfigReader.getProperty("test.username");
        String password = ConfigReader.getProperty("test.password");

        loginPage.iniciarSesion(usuario, password);

        String urlActual = driver.getCurrentUrl();

        Assert.assertFalse(
                urlActual.contains("/login?error"),
                "El portal rechazo las credenciales configuradas."
        );

        Assert.assertFalse(
                urlActual.endsWith("/login"),
                "La sesion no abandono la pantalla de login."
        );
    }
}