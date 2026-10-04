package com.botengine.framework.automatizaciones.zentramed.pages;

import com.botengine.framework.utils.EvidenciaPaso;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object correspondiente al formulario de autenticación
 * de Zentra MED.
 *
 * La clase centraliza los selectores y las operaciones necesarias
 * para iniciar sesión en la aplicación, evitando que los tests
 * dependan directamente de la estructura HTML.
 */
public class ZentraMedLogin {

    private final WebDriver driver;

    /*
     * Selectores obtenidos del HTML real de Zentra MED.
     *
     * Correo:
     * <input id="email" ...>
     *
     * Contraseña:
     * <input id="password" ...>
     *
     * Formulario:
     * <form id="loginForm">
     */
    private final By formularioLogin =
            By.id("loginForm");

    private final By inputCorreo =
            By.id("email");

    private final By inputPassword =
            By.id("password");

    private final By botonIngresar =
            By.cssSelector(
                    "#loginForm button[type='submit']"
            );

    private final By alertaError =
            By.cssSelector(
                    "#alertContainer .alert.error"
            );

    private final By alertaExito =
            By.cssSelector(
                    "#alertContainer .alert.success"
            );

    public ZentraMedLogin(
            WebDriver driver) {

        if (driver == null) {

            throw new IllegalArgumentException(
                    "WebDriver no puede ser nulo."
            );
        }

        this.driver = driver;
    }

    /**
     * Espera hasta que el formulario de autenticación
     * se encuentre disponible para interactuar.
     */
    public void esperarFormulario(
            Duration timeout) {

        try {

            WebDriverWait wait =
                    new WebDriverWait(
                            driver,
                            timeout
                    );

            wait.until(
                    ExpectedConditions
                            .visibilityOfElementLocated(
                                    inputCorreo
                            )
            );

            wait.until(
                    ExpectedConditions
                            .visibilityOfElementLocated(
                                    inputPassword
                            )
            );

            wait.until(
                    ExpectedConditions
                            .elementToBeClickable(
                                    botonIngresar
                            )
            );

        } catch (TimeoutException exception) {

            throw new IllegalStateException(
                    "El formulario de login de Zentra MED no estuvo disponible dentro del tiempo esperado.",
                    exception
            );

        } catch (WebDriverException exception) {

            throw new IllegalStateException(
                    "Error de Selenium mientras se esperaba el formulario de Zentra MED.",
                    exception
            );
        }
    }

    /**
     * Ingresa las credenciales y envía el formulario.
     *
     * Las credenciales recibidas provienen del servicio seguro
     * CredencialSitioService y nunca deben escribirse en logs.
     */
    public void ingresarUsuario(
            String correo) {

        WebElement elemento =
                driver.findElement(
                        inputCorreo
                );

        elemento.clear();

        elemento.sendKeys(
                correo
        );
    }

    public void ingresarPassword(
            String password) {

        WebElement elemento =
                driver.findElement(
                        inputPassword
                );

        elemento.clear();

        elemento.sendKeys(
                password
        );
    }

    public void presionarIngresar() {

        driver.findElement(
                botonIngresar
        )
        .click();
    }
    public void iniciarSesion(
            String correo,
            String password) {

        if (correo == null
                || correo.isBlank()) {

            throw new IllegalArgumentException(
                    "El usuario de Zentra MED no puede estar vacío."
            );
        }

        if (password == null
                || password.isBlank()) {

            throw new IllegalArgumentException(
                    "La contraseña de Zentra MED no puede estar vacía."
            );
        }

        try {

            /*
             * PASO 1:
             * Localizar el campo de correo mediante id="email".
             */
            WebElement correoElement =
                    driver.findElement(
                            inputCorreo
                    );

            correoElement.clear();

            correoElement.sendKeys(
                    correo
            );

            /*
             * PASO 2:
             * Localizar el campo de contraseña mediante
             * id="password".
             */
            WebElement passwordElement =
                    driver.findElement(
                            inputPassword
                    );

            passwordElement.clear();

            passwordElement.sendKeys(
                    password
            );

            /*
             * PASO 3:
             * Presionar el botón Ingresar contenido
             * dentro de #loginForm.
             */
            WebElement boton =
                    driver.findElement(
                            botonIngresar
                    );

            boton.click();

        } catch (NoSuchElementException exception) {

            throw new IllegalStateException(
                    "No fue posible localizar uno de los elementos requeridos para el login de Zentra MED.",
                    exception
            );

        } catch (WebDriverException exception) {

            throw new IllegalStateException(
                    "Selenium no pudo completar el ingreso de credenciales en Zentra MED.",
                    exception
            );
        }
    }

    /**
     * Comprueba si Zentra MED creó el token utilizado por
     * su frontend después de una autenticación correcta.
     */
    /**
     * Evidencia inicial de la pantalla de acceso.
     */
    public void capturarIngresoPortal() {

        EvidenciaPaso.capturar(
                driver,
                "Ingreso al portal"
        );
    }

    /**
     * Registra el usuario de prueba y captura el resultado visual.
     */
    public void ingresarUsuarioConEvidencia(
            String usuario) {

        WebElement elemento =
                driver.findElement(
                        By.id(
                                "email"
                        )
                );

        elemento.clear();

        elemento.sendKeys(
                usuario
        );

        EvidenciaPaso.capturar(
                driver,
                "Ingreso de usuario"
        );
    }

    /**
     * Registra la contraseña configurada.
     *
     * El valor no se almacena en el reporte.
     * La propia interfaz web mantiene el campo enmascarado.
     */
    public void ingresarPasswordConEvidencia(
            String password) {

        WebElement elemento =
                driver.findElement(
                        By.id(
                                "password"
                        )
                );

        elemento.clear();

        elemento.sendKeys(
                password
        );

        EvidenciaPaso.capturar(
                driver,
                "Ingreso de contrasena"
        );
    }

    /**
     * Presiona el boton de ingreso y captura el estado
     * inmediatamente posterior a la accion.
     */
    public void presionarLoginConEvidencia() {

        driver.findElement(
                By.cssSelector(
                        "#loginForm button[type='submit']"
                )
        )
        .click();

        EvidenciaPaso.capturar(
                driver,
                "Inicio de sesion"
        );
    }

    /**
     * Evidencia que confirma el acceso correcto al sistema.
     */
    public void capturarAccesoConfirmado() {

        EvidenciaPaso.capturar(
                driver,
                "Acceso confirmado"
        );
    }
    public boolean tokenDisponible() {

        try {

            JavascriptExecutor javascript =
                    (JavascriptExecutor) driver;

            Object token =
                    javascript.executeScript(
                            "return window.localStorage.getItem('zentra_token');"
                    );

            return token != null
                    && !token
                    .toString()
                    .isBlank();

        } catch (WebDriverException exception) {

            return false;
        }
    }

    /**
     * Una autenticación se considera correcta cuando existe
     * el token de Zentra MED o la aplicación redirige al dashboard.
     */
    public boolean loginExitoso() {

        try {

            String url =
                    driver
                            .getCurrentUrl()
                            .toLowerCase();

            return tokenDisponible()
                    || url.contains(
                            "/index.html"
                    );

        } catch (WebDriverException exception) {

            return false;
        }
    }

    public boolean errorVisible() {

        try {

            List<WebElement> errores =
                    driver.findElements(
                            alertaError
                    );

            return errores
                    .stream()
                    .anyMatch(
                            WebElement::isDisplayed
                    );

        } catch (WebDriverException exception) {

            return false;
        }
    }

    public String obtenerMensajeError() {

        try {

            List<WebElement> errores =
                    driver.findElements(
                            alertaError
                    );

            if (errores.isEmpty()) {

                return "Error de autenticación sin mensaje visible.";
            }

            return errores
                    .get(0)
                    .getText()
                    .trim();

        } catch (WebDriverException exception) {

            return "No fue posible recuperar el mensaje de error de Zentra MED.";
        }
    }

    public boolean alertaExitoVisible() {

        try {

            return driver
                    .findElements(
                            alertaExito
                    )
                    .stream()
                    .anyMatch(
                            WebElement::isDisplayed
                    );

        } catch (WebDriverException exception) {

            return false;
        }
    }

    public boolean formularioLoginVisible() {

        try {

            return driver
                    .findElements(
                            formularioLogin
                    )
                    .stream()
                    .anyMatch(
                            WebElement::isDisplayed
                    );

        } catch (WebDriverException exception) {

            return false;
        }
    }
}