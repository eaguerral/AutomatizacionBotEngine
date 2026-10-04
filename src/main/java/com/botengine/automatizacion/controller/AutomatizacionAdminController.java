package com.botengine.automatizacion.controller;

import com.botengine.automatizacion.dto.AutomatizacionForm;
import com.botengine.automatizacion.service.AutomatizacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/automatizaciones/admin")
public class AutomatizacionAdminController {

    private final AutomatizacionService automatizacionService;

    public AutomatizacionAdminController(
            AutomatizacionService automatizacionService) {

        this.automatizacionService =
                automatizacionService;
    }

    @GetMapping
    public String listar(Model model) {

        model.addAttribute(
                "automatizaciones",
                automatizacionService.listarTodas()
        );

        return "automatizaciones-admin";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {

        model.addAttribute(
                "automatizacionForm",
                new AutomatizacionForm()
        );

        return "automatizacion-form";
    }

    @PostMapping("/nueva")
    public String crear(
            @ModelAttribute
            AutomatizacionForm automatizacionForm,
            Model model) {

        try {

            automatizacionService.crear(
                    automatizacionForm
            );

            return "redirect:/automatizaciones/admin";

        } catch (IllegalArgumentException exception) {

            model.addAttribute(
                    "error",
                    exception.getMessage()
            );

            return "automatizacion-form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "automatizacionForm",
                automatizacionService
                        .obtenerFormularioEdicion(id)
        );

        return "automatizacion-editar";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(
            @PathVariable Long id,
            @ModelAttribute
            AutomatizacionForm automatizacionForm,
            Model model) {

        try {

            automatizacionService.actualizar(
                    id,
                    automatizacionForm
            );

            return "redirect:/automatizaciones/admin";

        } catch (IllegalArgumentException exception) {

            automatizacionForm.setId(id);

            model.addAttribute(
                    "error",
                    exception.getMessage()
            );

            return "automatizacion-editar";
        }
    }
}