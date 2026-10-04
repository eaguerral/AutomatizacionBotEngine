package com.botengine.automatizacion.controller;

import com.botengine.automatizacion.model.ResultadoEjecucion;
import com.botengine.automatizacion.repository.EmpresaRepository;
import com.botengine.automatizacion.repository.ResultadoEjecucionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;

@Controller
public class ReportesController {

    private final EmpresaRepository empresaRepository;
    private final ResultadoEjecucionRepository resultadoRepository;

    public ReportesController(
            EmpresaRepository empresaRepository,
            ResultadoEjecucionRepository resultadoRepository) {

        this.empresaRepository =
                empresaRepository;

        this.resultadoRepository =
                resultadoRepository;
    }

    @GetMapping("/reportes")
    public String reportes(
            @RequestParam(
                    required = false
            )
            String empresa,
            Model model) {

        model.addAttribute(
                "empresas",
                empresaRepository
                        .findAllByOrderByNombreAsc()
        );

        String codigo =
                empresa == null
                        ? ""
                        : empresa
                                .trim()
                                .toUpperCase(
                                        Locale.ROOT
                                );

        List<ResultadoEjecucion> ejecuciones =
                codigo.isBlank()
                        ? List.of()
                        : resultadoRepository
                                .findByAutomatizacion_SitioCodigoOrderByFechaCreacionDesc(
                                        codigo
                                );

        model.addAttribute(
                "empresaSeleccionada",
                codigo
        );

        model.addAttribute(
                "ejecuciones",
                ejecuciones
        );

        return "reportes";
    }
}