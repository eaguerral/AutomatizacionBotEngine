package com.botengine.automatizacion.controller;

import com.botengine.automatizacion.dto.UsuarioEditForm;
import com.botengine.automatizacion.dto.UsuarioForm;
import com.botengine.automatizacion.repository.RolRepository;
import com.botengine.automatizacion.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;

    public UsuarioController(
            UsuarioService usuarioService,
            RolRepository rolRepository) {

        this.usuarioService = usuarioService;
        this.rolRepository = rolRepository;
    }

    @GetMapping
    public String listarUsuarios(Model model) {

        model.addAttribute(
                "usuarios",
                usuarioService.listarTodos()
        );

        return "usuarios";
    }

    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model) {

        model.addAttribute(
                "usuarioForm",
                new UsuarioForm()
        );

        model.addAttribute(
                "roles",
                rolRepository.findAll()
        );

        return "usuario-form";
    }

    @PostMapping("/nuevo")
    public String crearUsuario(
            @ModelAttribute UsuarioForm usuarioForm,
            Model model) {

        try {

            usuarioService.crear(usuarioForm);

            return "redirect:/usuarios";

        } catch (IllegalArgumentException exception) {

            model.addAttribute(
                    "error",
                    exception.getMessage()
            );

            model.addAttribute(
                    "roles",
                    rolRepository.findAll()
            );

            return "usuario-form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editarUsuario(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "usuarioForm",
                usuarioService.obtenerFormularioEdicion(id)
        );

        model.addAttribute(
                "roles",
                rolRepository.findAll()
        );

        return "usuario-editar";
    }

    @PostMapping("/{id}/editar")
    public String actualizarUsuario(
            @PathVariable Long id,
            @ModelAttribute UsuarioEditForm usuarioForm,
            Model model) {

        try {

            usuarioService.actualizar(
                    id,
                    usuarioForm
            );

            return "redirect:/usuarios";

        } catch (IllegalArgumentException exception) {

            usuarioForm.setId(id);

            model.addAttribute(
                    "error",
                    exception.getMessage()
            );

            model.addAttribute(
                    "roles",
                    rolRepository.findAll()
            );

            return "usuario-editar";
        }
    }
}