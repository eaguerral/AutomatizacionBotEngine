package com.botengine.automatizacion.controller;

import com.botengine.automatizacion.model.ResultadoEjecucion;
import com.botengine.automatizacion.service.AutomationExecutionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/automatizaciones")
public class AutomationController {

    private final AutomationExecutionService automationExecutionService;

    public AutomationController(
            AutomationExecutionService automationExecutionService) {

        this.automationExecutionService = automationExecutionService;
    }

    @GetMapping
    public String automatizaciones() {
        return "automatizaciones";
    }

    @PostMapping("/login-test/ejecutar")
    public String ejecutarLoginTest(Model model) {

        ResultadoEjecucion resultado =
                automationExecutionService.ejecutarLoginTest();

        model.addAttribute("resultado", resultado);

        return "resultado-ejecucion";
    }
}