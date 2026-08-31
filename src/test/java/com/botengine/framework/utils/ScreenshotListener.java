package com.botengine.framework.utils;

import com.botengine.framework.core.BaseTest;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Listener de TestNG para capturar evidencia cuando una prueba falla.
 */
public class ScreenshotListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        Object testInstance = result.getInstance();

        if (testInstance instanceof BaseTest baseTest && baseTest.getDriver() != null) {
            String scenarioName = result.getMethod().getMethodName();
            String evidence = ScreenshotUtil.takeScreenshot(baseTest.getDriver(), scenarioName);
            System.out.println("Evidencia generada por fallo: " + evidence);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        Object testInstance = result.getInstance();

        if (testInstance instanceof BaseTest baseTest && baseTest.getDriver() != null) {
            String scenarioName = result.getMethod().getMethodName();
            String evidence = ScreenshotUtil.takeScreenshot(baseTest.getDriver(), scenarioName);
            System.out.println("Evidencia generada por ejecución exitosa: " + evidence);
        }
    }
}
