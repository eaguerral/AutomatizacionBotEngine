package com.botengine.automatizacion.controller;

import com.botengine.automatizacion.dto.InsumoAutomatizacionForm;
import com.botengine.automatizacion.model.Empresa;
import com.botengine.automatizacion.repository.EmpresaRepository;
import com.botengine.automatizacion.service.InsumoAutomatizacionService;
import com.botengine.automatizacion.service.RecursoAutomatizacionDetectorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping({
        "/insumos",
        "/administracion/configuracion/clases"
})
public class InsumoAutomatizacionController {

    private final InsumoAutomatizacionService service;
    private final EmpresaRepository empresaRepository;
    private final RecursoAutomatizacionDetectorService detectorService;

    public InsumoAutomatizacionController(
            InsumoAutomatizacionService service,
            EmpresaRepository empresaRepository,
            RecursoAutomatizacionDetectorService detectorService) {

        this.service = service;
        this.empresaRepository = empresaRepository;
        this.detectorService = detectorService;
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

        return "insumos";
    }

    @GetMapping("/recursos")
    @ResponseBody
    public Map<String, Object> detectarRecursos(
            @RequestParam Long empresaId) {

        Empresa empresa =
                empresaRepository
                        .findById(
                                empresaId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "La empresa seleccionada no existe."
                                )
                        );

        if (!empresa.isActivo()) {

            throw new IllegalArgumentException(
                    "La empresa seleccionada está inactiva."
            );
        }

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put(
                "empresaId",
                empresa.getId()
        );

        respuesta.put(
                "empresa",
                empresa.getNombre()
        );

        respuesta.put(
                "carpeta",
                empresa.getCarpeta()
        );

        respuesta.put(
                "clases",
                detectorService.detectarClases(
                        empresa.getCarpeta()
                )
        );

        respuesta.put(
                "xml",
                detectorService.detectarXml(
                        empresa.getCarpeta()
                )
        );

        return respuesta;
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute("insumoForm")
            InsumoAutomatizacionForm form,
            @RequestParam(
                    name = "clase",
                    required = false
            )
            String clase,
            @RequestParam(
                    name = "archivoXml",
                    required = false
            )
            String archivoXml,
            Model model) {

        try {

            service.guardarConRecursos(
                    form,
                    clase,
                    archivoXml
            );

            return "redirect:/administracion/configuracion/clases";

        } catch (IllegalArgumentException exception) {

            model.addAttribute(
                    "error",
                    exception.getMessage()
            );

            model.addAttribute(
                    "insumos",
                    service.listarTodos()
            );

            model.addAttribute(
                    "empresas",
                    empresaRepository
                            .findByActivoTrueOrderByNombreAsc()
            );

            model.addAttribute(
                    "insumoForm",
                    form
            );

            model.addAttribute(
                    "mostrarFormulario",
                    true
            );

            return "insumos";
        }
    }
    @PostMapping("/{id}/vincular")
    public String vincular(
            @PathVariable Long id,
            @RequestParam String clase,
            @RequestParam String archivoXml) {

        service.vincularRecursos(
                id,
                clase,
                archivoXml
        );

        return "redirect:/administracion/configuracion/clases";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(
            @PathVariable Long id,
            Model model) {

        try {

            service.eliminar(
                    id
            );

            return "redirect:/administracion/configuracion/clases";

        } catch (IllegalArgumentException exception) {

            model.addAttribute(
                    "error",
                    exception.getMessage()
            );

            cargarVista(
                    model,
                    null
            );

            return "insumos";
        }
    }

    private void cargarVista(
            Model model,
            Long editar) {

        model.addAttribute(
                "insumos",
                service.listarTodos()
        );

        model.addAttribute(
                "empresas",
                empresaRepository
                        .findByActivoTrueOrderByNombreAsc()
        );

        if (editar != null) {

            model.addAttribute(
                    "insumoForm",
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
                    "insumoForm",
                    new InsumoAutomatizacionForm()
            );

            model.addAttribute(
                    "mostrarFormulario",
                    false
            );
        }
    }
}