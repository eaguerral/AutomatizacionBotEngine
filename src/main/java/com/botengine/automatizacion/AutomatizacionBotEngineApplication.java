package com.botengine.automatizacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicacion principal del portal AutomatizacionBotEngine.
 *
 * Se incluyen los paquetes de administracion del portal y
 * los componentes tecnicos del framework de automatizacion.
 */
@SpringBootApplication(
        scanBasePackages = {
                "com.botengine.automatizacion",
                "com.botengine.framework"
        }
)
public class AutomatizacionBotEngineApplication {

    public static void main(
            String[] args) {

        SpringApplication.run(
                AutomatizacionBotEngineApplication.class,
                args
        );
    }
}