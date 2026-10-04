package com.botengine.automatizacion.controller;

import com.botengine.automatizacion.model.Automatizacion;
import com.botengine.automatizacion.model.ResultadoEjecucion;
import com.botengine.automatizacion.model.Usuario;
import com.botengine.automatizacion.model.UsuarioAutomatizacion;
import com.botengine.automatizacion.repository.UsuarioRepository;
import com.botengine.automatizacion.service.AsignacionAutomatizacionService;
import com.botengine.automatizacion.service.AutomatizacionService;
import com.botengine.automatizacion.service.AutomationExecutionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/automatizaciones")
public class AutomationController {

    private final AutomationExecutionService automationExecutionService;
    private final AutomatizacionService automatizacionService;
    private final AsignacionAutomatizacionService asignacionService;
    private final UsuarioRepository usuarioRepository;

    public AutomationController(
            AutomationExecutionService automationExecutionService,
            AutomatizacionService automatizacionService,
            AsignacionAutomatizacionService asignacionService,
            UsuarioRepository usuarioRepository) {

        this.automationExecutionService =
                automationExecutionService;

        this.automatizacionService =
                automatizacionService;

        this.asignacionService =
                asignacionService;

        this.usuarioRepository =
                usuarioRepository;
    }

    @GetMapping
    public String automatizaciones(
            Authentication authentication,
            Model model) {

        Usuario usuario =
                buscarUsuario(
                        authentication.getName()
                );

        String rol =
                usuario.getRol().getNombre();

        List<Automatizacion> automatizaciones =
                asignacionService
                        .listarAsignacionesActivasDeUsuario(
                                usuario.getId()
                        )
                        .stream()
                        .map(
                                UsuarioAutomatizacion::getAutomatizacion
                        )
                        .toList();

        boolean puedeEjecutar =
                "TECNICO".equals(rol)
                || "ADMIN".equals(rol)
                || "RESPONSABLE_TECNICO".equals(rol);

        model.addAttribute(
                "automatizaciones",
                automatizaciones
        );

        model.addAttribute(
                "rol",
                rol
        );

        model.addAttribute(
                "puedeEjecutar",
                puedeEjecutar
        );

        return "automatizaciones";
    }

    @PostMapping("/{id}/ejecutar")
    public String ejecutar(
            @PathVariable Long id,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            Usuario usuario =
                    buscarUsuario(
                            authentication.getName()
                    );

            String rol =
                    usuario.getRol().getNombre();

            if (!"TECNICO".equals(rol)
                    && !"ADMIN".equals(rol)
                    && !"RESPONSABLE_TECNICO".equals(rol)) {

                throw new IllegalArgumentException(
                        "Su rol no tiene permiso para ejecutar automatizaciones."
                );
            }

            Automatizacion automatizacion =
                    automatizacionService
                            .buscarPorId(id);

            if (!automatizacion.isActivo()) {

                throw new IllegalArgumentException(
                        "La automatizacion se encuentra inactiva."
                );
            }

            if (!asignacionService
                    .tieneAsignacionActiva(
                            usuario.getId(),
                            automatizacion.getId()
                    )) {

                throw new IllegalArgumentException(
                        "No tiene autorizacion para ejecutar esta automatizacion."
                );
            }

            ResultadoEjecucion resultado =
                    automationExecutionService
                            .ejecutar(
                                    automatizacion,
                                    usuario
                            );

            model.addAttribute(
                    "resultado",
                    resultado
            );

            return "resultado-ejecucion";

        } catch (IllegalArgumentException exception) {

            redirectAttributes
                    .addFlashAttribute(
                            "error",
                            exception.getMessage()
                    );

            return "redirect:/automatizaciones";
        }
    }

    private Usuario buscarUsuario(
            String username) {

        return usuarioRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El usuario autenticado no existe."
                        )
                );
    }
}