package com.botengine.automatizacion.config;

import com.botengine.automatizacion.model.Empresa;
import com.botengine.automatizacion.repository.EmpresaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmpresaInicialConfig {

    @Bean
    public CommandLineRunner inicializarEmpresa(
            EmpresaRepository repository) {

        return args -> {

            if (!repository.existsByCodigo(
                    "ZENTRAMED")) {

                Empresa empresa =
                        new Empresa();

                empresa.setCodigo(
                        "ZENTRAMED"
                );

                empresa.setNombre(
                        "Zentra MED"
                );

                empresa.setCarpeta(
                        "zentramed"
                );

                empresa.setDescripcion(
                        "Sistema Zentra MED utilizado para automatizaciones funcionales."
                );

                empresa.setActivo(
                        true
                );

                repository.save(
                        empresa
                );
            }
        };
    }
}