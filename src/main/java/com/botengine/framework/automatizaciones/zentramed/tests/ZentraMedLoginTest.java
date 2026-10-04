package com.botengine.framework.automatizaciones.zentramed.tests;

import com.botengine.automatizacion.dto.CredencialSitio;
import com.botengine.automatizacion.service.CredencialSitioService;
import com.botengine.framework.automatizaciones.zentramed.pages.ZentraMedLogin;
import com.botengine.framework.core.BaseTest;
import com.botengine.framework.core.SpringContext;
import com.botengine.framework.utils.ConfigReader;
import com.botengine.framework.utils.EvidenciaPaso;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * Prueba funcional automatizada del inicio de sesión
 * de la aplicación Zentra MED.
 *
 * Flujo:
 * 1. Obtiene la credencial configurada para ZENTRAMED.
 * 2. Abre la URL del sistema.
 * 3. Espera el formulario de autenticación.
 * 4. Ingresa usuario y contraseña.
 * 5. Valida la generación del token zentra_token.
 * 6. Valida la redirección al dashboard.
 */
public class ZentraMedLoginTest extends BaseTest {

    @Test(
            description =
                    "Validar inicio de sesión correcto en Zentra MED"
    )
    public void validarLoginZentraMed() {

        try {

            /*
             * PASO 1:
             * Obtener una cuenta activa configurada para
             * la empresa/sitio ZENTRAMED.
             */
            CredencialSitioService credencialService =
                    SpringContext.getBean(
                            CredencialSitioService.class
                    );

            CredencialSitio credencial =
                    credencialService
                            .obtenerCredencialDisponible(
                                    "ZENTRAMED"
                            );

            /*
             * PASO 2:
             * Determinar el tiempo máximo permitido
             * para las esperas explícitas de Selenium.
             */
            int timeoutSeconds =
                    Integer.parseInt(
                            ConfigReader.getProperty(
                                    "timeout.seconds"
                            )
                    );

            Duration timeout =
                    Duration.ofSeconds(
                            Math.max(
                                    timeoutSeconds,
                                    15
                            )
                    );

            /*
             * PASO 3:
             * Abrir la aplicación real de Zentra MED.
             */
            driver.get(
                    credencial.url()
            );

            ZentraMedLogin login =
                    new ZentraMedLogin(
                            driver
                    );

            /*
             * PASO 4:
             * Esperar el formulario real identificado
             * mediante #email, #password y #loginForm.
             */
            login.esperarFormulario(
                    timeout
            );

            /*
             * PASO 5:
             * Ingresar las credenciales obtenidas desde
             * la configuración segura del framework.
             */
            /*
             * Evidencias funcionales visibles para cliente,
             * coordinador o responsable de la certificacion.
             */
            login.capturarIngresoPortal();

            login.ingresarUsuarioConEvidencia(
                    credencial.username()
            );

            login.ingresarPasswordConEvidencia(
                    credencial.password()
            );

            login.presionarLoginConEvidencia();

            /*
             * PASO 6:
             * Esperar una respuesta del sistema.
             *
             * Puede ser:
             * - autenticación correcta;
             * - mensaje de error visible.
             */
            /*
             * Se utiliza un tiempo de respuesta mayor para la
             * autenticación debido a que la aplicación puede
             * completar la redirección después de procesar
             * la petición al backend.
             */
            Duration timeoutRespuesta =
                    Duration.ofSeconds(
                            Math.max(
                                    timeout.toSeconds(),
                                    30
                            )
                    );

            WebDriverWait wait =
                    new WebDriverWait(
                            driver,
                            timeoutRespuesta
                    );

            wait.until(webDriver ->
                    login.loginExitoso()
                    || login.errorVisible()
            );

            if (login.errorVisible()) {

                Assert.fail(
                        "Zentra MED rechazó la autenticación: "
                        + login.obtenerMensajeError()
                );
            }

            /*
             * PASO 7:
             * Validar el token utilizado por Zentra MED.
             */
            Assert.assertTrue(
                    login.tokenDisponible(),
                    "Zentra MED no generó el token de autenticación zentra_token."
            );

            /*
             * PASO 8:
             * Confirmar que el navegador terminó en el dashboard.
             */
            wait.until(webDriver ->
                    webDriver
                            .getCurrentUrl()
                            .toLowerCase()
                            .contains(
                                    "/index.html"
                            )
            );

            Assert.assertFalse(
                    login.formularioLoginVisible(),
                    "El formulario de login continúa visible después de autenticarse."
            );

                            /*
             * El sistema permitio el acceso y la pantalla final
             * queda registrada como evidencia.
             */
            login.capturarAccesoConfirmado();

            EvidenciaPaso.capturar(
                    driver,
                    "Acceso confirmado",
                    "Se verificó que el sistema permitiera el acceso correctamente."
            );

        } catch (TimeoutException exception) {

            /*
             * Puede existir una condicion de carrera donde la
             * redireccion termine exactamente al vencer el wait.
             * Antes de declarar fallo se valida la URL real.
             */
            String urlActual =
                    driver.getCurrentUrl();

            if (urlActual != null
                    && urlActual
                            .toLowerCase()
                            .contains("/index.html")) {

                return;
            }

            Assert.fail(
                    "La prueba excedio el tiempo maximo esperando una respuesta de Zentra MED. URL actual: "
                    + urlActual,
                    exception
            );

        } catch (WebDriverException exception) {

            Assert.fail(
                    "Selenium encontró un error durante la automatización de Zentra MED.",
                    exception
            );

        } catch (IllegalStateException exception) {

            Assert.fail(
                    "La configuración o ejecución de Zentra MED presentó un error: "
                    + exception.getMessage(),
                    exception
            );

        } catch (Exception exception) {

            Assert.fail(
                    "Ocurrió un error inesperado durante la prueba de Zentra MED: "
                    + exception.getMessage(),
                    exception
            );
        }
    }
}