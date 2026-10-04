package com.botengine.framework.utils;

import com.botengine.framework.core.BaseTest;
import org.testng.IConfigurationListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Genera evidencia adicional solamente cuando ocurre un fallo.
 *
 * Las evidencias exitosas se registran durante cada paso
 * funcional mediante EvidenciaPaso.
 */
public class ScreenshotListener
        implements ITestListener, IConfigurationListener {

    private final String directorioCapturas;

    public ScreenshotListener() {

        this.directorioCapturas =
                null;
    }

    public ScreenshotListener(
            String directorioCapturas) {

        this.directorioCapturas =
                directorioCapturas;
    }

    @Override
    public void onTestFailure(
            ITestResult result) {

        capturarFallo(
                result,
                "Error durante la ejecucion"
        );
    }

    @Override
    public void onTestSuccess(
            ITestResult result) {

        /*
         * No se genera una captura adicional.
         * Los pasos exitosos ya fueron documentados individualmente.
         */
    }

    @Override
    public void onConfigurationFailure(
            ITestResult result) {

        capturarFallo(
                result,
                "Error de configuracion"
        );
    }

    private void capturarFallo(
            ITestResult result,
            String titulo) {

        Object instancia =
                result.getInstance();

        if (!(instancia instanceof BaseTest baseTest)
                || baseTest.getDriver() == null) {

            return;
        }

        String evidencia;

        if (EvidenciaPaso.activa()) {

            evidencia =
                    EvidenciaPaso.capturar(
                            baseTest.getDriver(),
                            titulo
                    );

        } else if (directorioCapturas != null
                && !directorioCapturas.isBlank()) {

            evidencia =
                    ScreenshotUtil.takeScreenshot(
                            baseTest.getDriver(),
                            "error_ejecucion",
                            directorioCapturas
                    );

        } else {

            evidencia =
                    ScreenshotUtil.takeScreenshot(
                            baseTest.getDriver(),
                            "error_ejecucion"
                    );
        }

        System.out.println(
                "Evidencia de error generada: "
                + evidencia
        );
    }
}