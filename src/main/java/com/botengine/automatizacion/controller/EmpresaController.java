package com.botengine.automatizacion.controller;

import com.botengine.automatizacion.dto.EmpresaForm;
import com.botengine.automatizacion.service.EmpresaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/administracion/configuracion/empresas")
public class EmpresaController {

    private final EmpresaService service;

    public EmpresaController(
            EmpresaService service) {

        this.service = service;
    }

    @GetMapping
    public String listar(
            @RequestParam(
                    name = "editar",
                    required = false
            )
            Long editar,
            Model model) {

        cargarVista(
                model,
                editar
        );

        return "empresas";
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute("empresaForm")
            EmpresaForm form,
            Model model) {

        try {

            service.guardar(
                    form
            );

            return "redirect:/administracion/configuracion/empresas";

        } catch (IllegalArgumentException exception) {

            model.addAttribute(
                    "error",
                    exception.getMessage()
            );

            model.addAttribute(
                    "empresas",
                    service.listarTodas()
            );

            model.addAttribute(
                    "empresaForm",
                    form
            );

            model.addAttribute(
                    "mostrarFormulario",
                    true
            );

            return "empresas";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(
            @PathVariable Long id,
            Model model) {

        try {

            service.eliminar(
                    id
            );

            return "redirect:/administracion/configuracion/empresas";

        } catch (IllegalArgumentException exception) {

            model.addAttribute(
                    "error",
                    exception.getMessage()
            );

            cargarVista(
                    model,
                    null
            );

            return "empresas";
        }
    }

    private void cargarVista(
            Model model,
            Long editar) {

        model.addAttribute(
                "empresas",
                service.listarTodas()
        );

        if (editar != null) {

            model.addAttribute(
                    "empresaForm",
                    service.obtenerFormulario(
                            editar
                    )
            );

            model.addAttribute(
                    "mostrarFormulario",
                    true
            );

        } else {

            model.addAttribute(
                    "empresaForm",
                    new EmpresaForm()
            );

            model.addAttribute(
                    "mostrarFormulario",
                    false
            );
        }
    }
}