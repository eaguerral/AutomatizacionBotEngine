package com.botengine.automatizacion.controller;

import com.botengine.automatizacion.dto.PerfilForm;
import com.botengine.automatizacion.model.Usuario;
import com.botengine.automatizacion.service.PerfilService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CuentaController {

    private final PerfilService perfilService;

    public CuentaController(
            PerfilService perfilService) {

        this.perfilService = perfilService;
    }

    @GetMapping("/perfil")
    public String perfil(
            Authentication authentication,
            Model model) {

        String username = authentication.getName();

        Usuario usuario =
                perfilService.buscarPorUsername(username);

        model.addAttribute(
                "usuario",
                usuario
        );

        model.addAttribute(
                "perfilForm",
                perfilService.obtenerFormulario(username)
        );

        return "perfil";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(
            Authentication authentication,
            @ModelAttribute PerfilForm perfilForm,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            perfilService.actualizarPerfil(
                    authentication.getName(),
                    perfilForm
            );

            redirectAttributes.addFlashAttribute(
                    "exito",
                    "Perfil actualizado correctamente."
            );

            return "redirect:/perfil";

        } catch (IllegalArgumentException exception) {

            model.addAttribute(
                    "error",
                    exception.getMessage()
            );

            model.addAttribute(
                    "usuario",
                    perfilService.buscarPorUsername(
                            authentication.getName()
                    )
            );

            return "perfil";
        }
    }

    @GetMapping("/configuracion")
    public String configuracion(
            Authentication authentication,
            Model model) {

        model.addAttribute(
                "usuario",
                perfilService.buscarPorUsername(
                        authentication.getName()
                )
        );

        return "configuracion-cuenta";
    }
}