package com.botengine.automatizacion.service;

import com.botengine.automatizacion.dto.PerfilForm;
import com.botengine.automatizacion.model.Usuario;
import com.botengine.automatizacion.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PerfilService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public PerfilService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario buscarPorUsername(String username) {

        return usuarioRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El usuario autenticado no existe."
                        )
                );
    }

    public PerfilForm obtenerFormulario(String username) {

        Usuario usuario = buscarPorUsername(username);

        PerfilForm form = new PerfilForm();

        form.setNombreCompleto(
                usuario.getNombreCompleto()
        );

        form.setEmail(
                usuario.getEmail()
        );

        return form;
    }

    public void actualizarPerfil(
            String username,
            PerfilForm form) {

        Usuario usuario = buscarPorUsername(username);

        if (form.getNombreCompleto() == null
                || form.getNombreCompleto().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre completo es obligatorio."
            );
        }

        String email = null;

        if (form.getEmail() != null
                && !form.getEmail().isBlank()) {

            email = form.getEmail().trim();

            if (usuarioRepository.existsByEmailAndIdNot(
                    email,
                    usuario.getId())) {

                throw new IllegalArgumentException(
                        "El correo ya se encuentra registrado."
                );
            }
        }

        usuario.setNombreCompleto(
                form.getNombreCompleto().trim()
        );

        usuario.setEmail(email);

        if (form.getPassword() != null
                && !form.getPassword().isBlank()) {

            if (form.getPassword().length() < 8) {

                throw new IllegalArgumentException(
                        "La nueva contraseña debe contener al menos 8 caracteres."
                );
            }

            usuario.setPassword(
                    passwordEncoder.encode(
                            form.getPassword()
                    )
            );
        }

        usuarioRepository.save(usuario);
    }
}