package com.botengine.automatizacion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConfiguracionController {

    @GetMapping("/administracion/configuracion")
    public String configuracion() {

        return "configuracion-framework";
    }
}