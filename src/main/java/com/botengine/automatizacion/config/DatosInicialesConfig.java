package com.botengine.automatizacion.config;

import com.botengine.automatizacion.model.Rol;
import com.botengine.automatizacion.model.Usuario;
import com.botengine.automatizacion.repository.RolRepository;
import com.botengine.automatizacion.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatosInicialesConfig {

    @Bean
    CommandLineRunner cargarDatosIniciales(
            RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${botengine.admin.username}") String adminUsername,
            @Value("${botengine.admin.password}") String adminPassword) {

        return args -> {

            crearRolSiNoExiste(
                    rolRepository,
                    "ADMIN",
                    "Administrador del portal y de la configuracion general."
            );

            crearRolSiNoExiste(
                    rolRepository,
                    "TECNICO",
                    "Usuario tecnico autorizado para ejecutar automatizaciones asignadas."
            );

            crearRolSiNoExiste(
                    rolRepository,
                    "RESPONSABLE_TECNICO",
                    "Responsable de revisar resultados, historial e indicadores."
            );

            if (!usuarioRepository.existsByUsername(adminUsername)) {

                Rol rolAdmin = rolRepository.findByNombre("ADMIN")
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No se encontro el rol ADMIN."
                                )
                        );

                Usuario administrador = new Usuario();

                administrador.setNombreCompleto(
                        "Administrador Bot Engine"
                );

                administrador.setUsername(
                        adminUsername
                );

                administrador.setPassword(
                        passwordEncoder.encode(adminPassword)
                );

                administrador.setActivo(true);
                administrador.setRol(rolAdmin);

                usuarioRepository.save(administrador);
            }
        };
    }

    private void crearRolSiNoExiste(
            RolRepository rolRepository,
            String nombre,
            String descripcion) {

        if (!rolRepository.existsByNombre(nombre)) {

            Rol rol = new Rol(
                    nombre,
                    descripcion
            );

            rolRepository.save(rol);
        }
    }
}