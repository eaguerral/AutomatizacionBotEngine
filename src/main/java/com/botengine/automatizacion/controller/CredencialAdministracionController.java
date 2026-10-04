package com.botengine.automatizacion.controller;

import com.botengine.automatizacion.dto.CredencialSitioForm;
import com.botengine.automatizacion.model.Empresa;
import com.botengine.automatizacion.service.CuentaSitioService;
import com.botengine.automatizacion.repository.EmpresaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/administracion/configuracion/credenciales")
public class CredencialAdministracionController {

    private final CuentaSitioService service;
    private final EmpresaRepository empresaRepository;

    public CredencialAdministracionController(
            CuentaSitioService service,
            EmpresaRepository empresaRepository) {

        this.service = service;
        this.empresaRepository =
                empresaRepository;
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

        return "credenciales";
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute("credencialForm")
            CredencialSitioForm form,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            service.guardar(
                    form
            );

            redirectAttributes.addFlashAttribute(
                    "exito",
                    "Credencial guardada correctamente."
            );

            return "redirect:/administracion/configuracion/credenciales";

        } catch (
                IllegalArgumentException
                | IllegalStateException exception) {

            model.addAttribute(
                    "error",
                    exception.getMessage()
            );

            cargarVistaComun(
                    model
            );

            model.addAttribute(
                    "credencialForm",
                    form
            );

            model.addAttribute(
                    "mostrarFormulario",
                    true
            );

            return "credenciales";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        service.cambiarEstado(
                id
        );

        redirectAttributes.addFlashAttribute(
                "exito",
                "Estado actualizado correctamente."
        );

        return "redirect:/administracion/configuracion/credenciales";
    }

    private void cargarVista(
            Model model,
            Long editar) {

        cargarVistaComun(
                model
        );

        if (editar != null) {

            model.addAttribute(
                    "credencialForm",
                    service.obtenerFormulario(
                            editar
                    )
            );

            model.addAttribute(
                    "mostrarFormulario",
                    true
            );

            return;
        }

        model.addAttribute(
                "credencialForm",
                new CredencialSitioForm()
        );

        model.addAttribute(
                "mostrarFormulario",
                false
        );
    }

    private void cargarVistaComun(
            Model model) {

        model.addAttribute(
                "credenciales",
                service.listarTodas()
        );

        model.addAttribute(
                "empresas",
                empresaRepository
                        .findByActivoTrueOrderByNombreAsc()
        );

        Map<String, String> nombresEmpresas =
                empresaRepository
                        .findAll()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        Empresa::getCodigo,
                                        Empresa::getNombre,
                                        (primero, segundo) ->
                                                primero
                                )
                        );

        model.addAttribute(
                "nombresEmpresas",
                nombresEmpresas
        );
    }
}