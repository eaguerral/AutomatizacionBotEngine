package com.botengine.automatizacion.service;

import com.botengine.automatizacion.dto.UsuarioEditForm;
import com.botengine.automatizacion.dto.UsuarioForm;
import com.botengine.automatizacion.model.Rol;
import com.botengine.automatizacion.model.Usuario;
import com.botengine.automatizacion.repository.RolRepository;
import com.botengine.automatizacion.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El usuario no existe."
                        )
                );
    }

    public Usuario crear(UsuarioForm form) {

        String username = form.getUsername().trim();

        if (usuarioRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "El nombre de usuario ya existe."
            );
        }

        String email = normalizarEmail(form.getEmail());

        if (email != null
                && usuarioRepository.existsByEmail(email)) {

            throw new IllegalArgumentException(
                    "El correo ya se encuentra registrado."
            );
        }

        if (form.getPassword() == null
                || form.getPassword().length() < 8) {

            throw new IllegalArgumentException(
                    "La contraseña debe contener al menos 8 caracteres."
            );
        }

        Rol rol = buscarRol(form.getRolId());

        Usuario usuario = new Usuario();

        usuario.setNombreCompleto(
                form.getNombreCompleto().trim()
        );

        usuario.setUsername(username);
        usuario.setEmail(email);

        usuario.setPassword(
                passwordEncoder.encode(
                        form.getPassword()
                )
        );

        usuario.setRol(rol);
        usuario.setActivo(form.isActivo());

        return usuarioRepository.save(usuario);
    }

    public UsuarioEditForm obtenerFormularioEdicion(Long id) {

        Usuario usuario = buscarPorId(id);

        UsuarioEditForm form = new UsuarioEditForm();

        form.setId(usuario.getId());
        form.setNombreCompleto(usuario.getNombreCompleto());
        form.setUsername(usuario.getUsername());
        form.setEmail(usuario.getEmail());
        form.setRolId(usuario.getRol().getId());
        form.setActivo(usuario.isActivo());

        return form;
    }

    public Usuario actualizar(
            Long id,
            UsuarioEditForm form) {

        Usuario usuario = buscarPorId(id);

        String username = form.getUsername().trim();

        if (usuarioRepository.existsByUsernameAndIdNot(
                username,
                id)) {

            throw new IllegalArgumentException(
                    "El nombre de usuario ya existe."
            );
        }

        String email = normalizarEmail(form.getEmail());

        if (email != null
                && usuarioRepository.existsByEmailAndIdNot(
                        email,
                        id)) {

            throw new IllegalArgumentException(
                    "El correo ya se encuentra registrado."
            );
        }

        Rol rol = buscarRol(form.getRolId());

        usuario.setNombreCompleto(
                form.getNombreCompleto().trim()
        );

        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setRol(rol);
        usuario.setActivo(form.isActivo());

        if (form.getPassword() != null
                && !form.getPassword().isBlank()) {

            if (form.getPassword().length() < 8) {

                throw new IllegalArgumentException(
                        "La contraseña debe contener al menos 8 caracteres."
                );
            }

            usuario.setPassword(
                    passwordEncoder.encode(
                            form.getPassword()
                    )
            );
        }

        return usuarioRepository.save(usuario);
    }

    private Rol buscarRol(Long rolId) {

        if (rolId == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un rol."
            );
        }

        return rolRepository.findById(rolId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El rol seleccionado no existe."
                        )
                );
    }

    private String normalizarEmail(String email) {

        if (email == null || email.isBlank()) {
            return null;
        }

        return email.trim();
    }
}