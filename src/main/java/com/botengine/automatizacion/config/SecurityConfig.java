package com.botengine.automatizacion.config;

import com.botengine.automatizacion.model.Usuario;
import com.botengine.automatizacion.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    "/login",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico"
                ).permitAll()

                .requestMatchers(
                    "/usuarios/**",
                    "/automatizaciones/admin/**",
                    "/asignaciones/**", "/insumos/**", "/administracion/configuracion/**"
                ).hasRole("ADMIN")

                .anyRequest().authenticated()
            )

            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            UsuarioRepository usuarioRepository) {

        return username -> {

            Usuario usuario =
                    usuarioRepository
                            .findByUsername(username)
                            .orElseThrow(() ->
                                    new UsernameNotFoundException(
                                            "Usuario no encontrado."
                                    )
                            );

            return User.builder()
                    .username(
                            usuario.getUsername()
                    )
                    .password(
                            usuario.getPassword()
                    )
                    .roles(
                            usuario.getRol().getNombre()
                    )
                    .disabled(
                            !usuario.isActivo()
                    )
                    .build();
        };
    }
}