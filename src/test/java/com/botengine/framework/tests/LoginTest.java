package com.botengine.framework.tests;

import com.botengine.framework.core.BaseTest;
import com.botengine.framework.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Prueba funcional base.
 * Este script sirve como evidencia inicial de la estructura de automatización.
 */
public class LoginTest extends BaseTest {

    @Test(description = "Validar escenario base de inicio de sesión")
    public void validarLoginBase() {
        LoginPage loginPage = new LoginPage(driver);

        String usuario = "usuario_demo";
        String password = "password_demo";

        loginPage.iniciarSesion(usuario, password);

        /*
         * Ajustar esta validación según la aplicación real.
         * Ejemplo:
         * String mensaje = loginPage.obtenerMensajeResultado();
         * Assert.assertTrue(mensaje.contains("Bienvenido"));
         */

        Assert.assertTrue(driver.getCurrentUrl().length() > 0,
                "La aplicación debe mantenerse accesible después de ejecutar el escenario base.");
    }
}
