package com.botengine.automatizacion.controller;

import com.botengine.automatizacion.model.EstadoEjecucion;
import com.botengine.automatizacion.model.ResultadoEjecucion;
import com.botengine.automatizacion.model.Usuario;
import com.botengine.automatizacion.repository.ResultadoEjecucionRepository;
import com.botengine.automatizacion.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class EjecucionConsultaController {

    private final ResultadoEjecucionRepository resultadoRepository;
    private final UsuarioRepository usuarioRepository;

    public EjecucionConsultaController(
            ResultadoEjecucionRepository resultadoRepository,
            UsuarioRepository usuarioRepository) {

        this.resultadoRepository =
                resultadoRepository;

        this.usuarioRepository =
                usuarioRepository;
    }

    @GetMapping("/ejecuciones")
    public String ejecuciones(
            Authentication authentication,
            Model model) {

        List<ResultadoEjecucion> resultados =
                obtenerResultadosVisibles(
                        authentication
                );

        List<ResultadoEjecucion> recientes =
                resultados.stream()
                        .limit(20)
                        .toList();

        long exitosas =
                recientes.stream()
                        .filter(resultado ->
                                resultado.getEstado()
                                        == EstadoEjecucion.EXITOSO)
                        .count();

        long fallidas =
                recientes.stream()
                        .filter(resultado ->
                                resultado.getEstado()
                                        == EstadoEjecucion.FALLIDO)
                        .count();

        long enEjecucion =
                recientes.stream()
                        .filter(resultado ->
                                resultado.getEstado()
                                        == EstadoEjecucion.EN_EJECUCION)
                        .count();

        model.addAttribute(
                "ejecuciones",
                recientes
        );

        model.addAttribute(
                "total",
                recientes.size()
        );

        model.addAttribute(
                "exitosas",
                exitosas
        );

        model.addAttribute(
                "fallidas",
                fallidas
        );

        model.addAttribute(
                "enEjecucion",
                enEjecucion
        );

        return "ejecuciones";
    }

    @GetMapping("/historial")
    public String historial(
            Authentication authentication,
            Model model) {

        model.addAttribute(
                "ejecuciones",
                obtenerResultadosVisibles(
                        authentication
                )
        );

        return "historial";
    }

    private List<ResultadoEjecucion>
    obtenerResultadosVisibles(
            Authentication authentication) {

        Usuario usuario =
                usuarioRepository
                        .findByUsername(
                                authentication.getName()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "El usuario autenticado no existe."
                                )
                        );

        String rol =
                usuario
                        .getRol()
                        .getNombre();

        if ("ADMIN".equals(rol)
                || "RESPONSABLE_TECNICO".equals(rol)) {

            return resultadoRepository
                    .findAllByOrderByFechaCreacionDesc();
        }

        return resultadoRepository
                .findByUsuarioIdOrderByFechaCreacionDesc(
                        usuario.getId()
                );
    }
}