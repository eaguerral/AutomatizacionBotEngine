package com.botengine.automatizacion.service;

import com.botengine.automatizacion.model.Automatizacion;
import com.botengine.automatizacion.model.Usuario;
import com.botengine.automatizacion.model.UsuarioAutomatizacion;
import com.botengine.automatizacion.repository.AutomatizacionRepository;
import com.botengine.automatizacion.repository.UsuarioAutomatizacionRepository;
import com.botengine.automatizacion.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsignacionAutomatizacionService {

    private final UsuarioRepository usuarioRepository;
    private final AutomatizacionRepository automatizacionRepository;
    private final UsuarioAutomatizacionRepository asignacionRepository;

    public AsignacionAutomatizacionService(
            UsuarioRepository usuarioRepository,
            AutomatizacionRepository automatizacionRepository,
            UsuarioAutomatizacionRepository asignacionRepository) {

        this.usuarioRepository =
                usuarioRepository;

        this.automatizacionRepository =
                automatizacionRepository;

        this.asignacionRepository =
                asignacionRepository;
    }

    public List<UsuarioAutomatizacion> listarTodas() {

        return asignacionRepository.findAll();
    }

    public List<Usuario> listarTecnicosActivos() {
        return usuarioRepository
                .findByActivoTrueOrderByNombreCompletoAsc()
                .stream()
                .filter(usuario -> {
                    String rol = usuario.getRol().getNombre();

                    return "TECNICO".equals(rol)
                            || "ADMIN".equals(rol)
                            || "RESPONSABLE_TECNICO".equals(rol);
                })
                .toList();
    }

    public List<Automatizacion>
    listarAutomatizacionesActivas() {

        return automatizacionRepository
                .findByActivoTrueOrderByNombreAsc();
    }

    public UsuarioAutomatizacion asignar(
            Long usuarioId,
            Long automatizacionId) {

        if (usuarioId == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un usuario."
            );
        }

        if (automatizacionId == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar una automatizacion."
            );
        }

        Usuario usuario =
                usuarioRepository
                        .findById(usuarioId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "El usuario no existe."
                                )
                        );

        if (!usuario.isActivo()) {

            throw new IllegalArgumentException(
                    "No se puede asignar una automatizacion a un usuario inactivo."
            );
        }

        String rolUsuario =
                usuario
                        .getRol()
                        .getNombre();

        if (!"TECNICO".equals(rolUsuario)
                && !"ADMIN".equals(rolUsuario)
                && !"RESPONSABLE_TECNICO".equals(rolUsuario)) {

            throw new IllegalArgumentException(
                    "El rol del usuario no tiene permiso para recibir automatizaciones."
            );
        }

        Automatizacion automatizacion =
                automatizacionRepository
                        .findById(automatizacionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "La automatizacion no existe."
                                )
                        );

        if (!automatizacion.isActivo()) {

            throw new IllegalArgumentException(
                    "No se puede asignar una automatizacion inactiva."
            );
        }

        return asignacionRepository
                .findByUsuarioIdAndAutomatizacionId(
                        usuarioId,
                        automatizacionId
                )
                .map(asignacion -> {

                    if (asignacion.isActivo()) {

                        throw new IllegalArgumentException(
                                "La automatizacion ya se encuentra asignada al usuario."
                        );
                    }

                    asignacion.setActivo(true);

                    return asignacionRepository.save(
                            asignacion
                    );
                })
                .orElseGet(() -> {

                    UsuarioAutomatizacion asignacion =
                            new UsuarioAutomatizacion();

                    asignacion.setUsuario(
                            usuario
                    );

                    asignacion.setAutomatizacion(
                            automatizacion
                    );

                    asignacion.setActivo(true);

                    return asignacionRepository.save(
                            asignacion
                    );
                });
    }

    public void desasignar(Long id) {

        UsuarioAutomatizacion asignacion =
                asignacionRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "La asignacion no existe."
                                )
                        );

        asignacion.setActivo(false);

        asignacionRepository.save(
                asignacion
        );
    }

    public List<UsuarioAutomatizacion>
    listarAsignacionesActivasDeUsuario(
            Long usuarioId) {

        return asignacionRepository
                .findByUsuarioIdAndActivoTrueAndAutomatizacionActivoTrueOrderByAutomatizacionNombreAsc(
                        usuarioId
                );
    }

    public boolean tieneAsignacionActiva(
            Long usuarioId,
            Long automatizacionId) {

        return asignacionRepository
                .existsByUsuarioIdAndAutomatizacionIdAndActivoTrue(
                        usuarioId,
                        automatizacionId
                );
    }
}