package com.botengine.automatizacion.controller;

import com.botengine.automatizacion.service.PublicacionAutomatizacionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/administracion/configuracion/clases")
public class PublicacionAutomatizacionController {

    private final PublicacionAutomatizacionService service;

    public PublicacionAutomatizacionController(
            PublicacionAutomatizacionService service) {

        this.service = service;
    }

    @PostMapping("/{id}/publicar")
    public String publicar(
            @PathVariable Long id) {

        service.publicar(
                id
        );

        return "redirect:/automatizaciones/admin";
    }
}