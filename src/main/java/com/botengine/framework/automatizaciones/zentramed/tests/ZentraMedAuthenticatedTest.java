package com.botengine.framework.automatizaciones.zentramed.tests;

import com.botengine.framework.automatizaciones.zentramed.pages.ZentraMedSesion;
import com.botengine.framework.core.BaseTest;
import org.testng.annotations.BeforeMethod;

/**
 * Base para requerimientos de Zentra MED que necesitan
 * autenticacion previa.
 *
 * La autenticacion funciona como precondicion y no aparece
 * como pasos del requerimiento ejecutado.
 */
public abstract class ZentraMedAuthenticatedTest
        extends BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void autenticarZentraMed() {

        ZentraMedSesion.asegurarSesion(
                driver
        );
    }
}