package com.botengine.automatizacion.config;

import com.botengine.automatizacion.model.Automatizacion;
import com.botengine.automatizacion.repository.AutomatizacionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutomatizacionInicialConfig {

    @Bean
    CommandLineRunner cargarAutomatizacionesIniciales(
            AutomatizacionRepository automatizacionRepository) {

        return args -> {

            String codigo = "LOGIN_TEST";

            if (!automatizacionRepository.existsByCodigo(codigo)) {

                Automatizacion automatizacion =
                        new Automatizacion();

                automatizacion.setCodigo(
                        codigo
                );

                automatizacion.setNombre(
                        "LoginTest"
                );

                automatizacion.setDescripcion(
                        "Validacion funcional base del proceso de inicio de sesion."
                );

                automatizacion.setClaseTest(
                        "com.botengine.framework.tests.LoginTest"
                );

                automatizacion.setActivo(true);

                automatizacionRepository.save(
                        automatizacion
                );
            }
        };
    }
}