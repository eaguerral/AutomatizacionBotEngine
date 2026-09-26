package com.botengine.framework.utils;

import com.botengine.framework.core.BaseTest;
import org.testng.IConfigurationListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ScreenshotListener implements ITestListener, IConfigurationListener {

    @Override
    public void onTestFailure(ITestResult result) {
        capturar(result, "fallo");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        capturar(result, "exito");
    }

    @Override
    public void onConfigurationFailure(ITestResult result) {
        capturar(result, "configuracion_fallida");
    }

    private void capturar(ITestResult result, String tipo) {

        Object instancia = result.getInstance();

        if (instancia instanceof BaseTest baseTest
                && baseTest.getDriver() != null) {

            String nombre =
                    result.getMethod().getMethodName() + "_" + tipo;

            String evidencia =
                    ScreenshotUtil.takeScreenshot(
                            baseTest.getDriver(),
                            nombre
                    );

            System.out.println(
                    "Evidencia generada: " + evidencia
            );
        }
    }
}