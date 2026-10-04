package com.botengine.automatizacion.controller;

import com.botengine.automatizacion.dto.AsignacionAutomatizacionForm;
import com.botengine.automatizacion.service.AsignacionAutomatizacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/asignaciones")
public class AsignacionAutomatizacionController {

    private final AsignacionAutomatizacionService asignacionService;

    public AsignacionAutomatizacionController(
            AsignacionAutomatizacionService asignacionService) {

        this.asignacionService =
                asignacionService;
    }

    @GetMapping
    public String listar(Model model) {

        cargarModelo(
                model,
                new AsignacionAutomatizacionForm()
        );

        return "asignaciones";
    }

    @PostMapping
    public String asignar(
            @ModelAttribute
            AsignacionAutomatizacionForm asignacionForm,
            Model model) {

        try {

            asignacionService.asignar(
                    asignacionForm.getUsuarioId(),
                    asignacionForm.getAutomatizacionId()
            );

            return "redirect:/asignaciones";

        } catch (IllegalArgumentException exception) {

            model.addAttribute(
                    "error",
                    exception.getMessage()
            );

            cargarModelo(
                    model,
                    asignacionForm
            );

            return "asignaciones";
        }
    }

    @PostMapping("/{id}/desasignar")
    public String desasignar(
            @PathVariable Long id) {

        asignacionService.desasignar(id);

        return "redirect:/asignaciones";
    }

    private void cargarModelo(
            Model model,
            AsignacionAutomatizacionForm form) {

        model.addAttribute(
                "asignacionForm",
                form
        );

        model.addAttribute(
                "usuarios",
                asignacionService
                        .listarTecnicosActivos()
        );

        model.addAttribute(
                "automatizaciones",
                asignacionService
                        .listarAutomatizacionesActivas()
        );

        model.addAttribute(
                "asignaciones",
                asignacionService
                        .listarTodas()
        );
    }
}