package com.botengine.automatizacion.service;

import com.botengine.automatizacion.model.ResultadoEjecucion;
import com.botengine.framework.tests.LoginTest;
import com.botengine.framework.utils.ScreenshotListener;
import org.springframework.stereotype.Service;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class AutomationExecutionService {

    public ResultadoEjecucion ejecutarLoginTest() {

        LocalDateTime inicio = LocalDateTime.now();

        TestListenerAdapter listener = new TestListenerAdapter();

        TestNG testNG = new TestNG();

        testNG.setTestClasses(new Class[]{LoginTest.class});
        testNG.addListener(listener);
        testNG.addListener(new ScreenshotListener());

        testNG.setUseDefaultListeners(true);
        testNG.setOutputDirectory("reportes/testng/portal");

        testNG.run();

        LocalDateTime fin = LocalDateTime.now();

        long duracion = Duration.between(inicio, fin).toMillis();

        boolean ejecucionConError =
                !listener.getFailedTests().isEmpty()
                || !listener.getSkippedTests().isEmpty();

        String estado = ejecucionConError
                ? "FALLIDO"
                : "EXITOSO";

        return new ResultadoEjecucion(
                "LoginTest",
                estado,
                inicio,
                fin,
                duracion
        );
    }
}