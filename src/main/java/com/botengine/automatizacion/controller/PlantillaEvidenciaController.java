package com.botengine.automatizacion.controller;

import com.botengine.automatizacion.service.PlantillaEvidenciaService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@Controller
@RequestMapping(
        "/administracion/configuracion/plantilla-evidencias"
)
public class PlantillaEvidenciaController {

    private final PlantillaEvidenciaService service;

    public PlantillaEvidenciaController(
            PlantillaEvidenciaService service) {

        this.service = service;
    }

    @GetMapping
    public String mostrar(
            Model model) {

        cargarDatos(
                model
        );

        return "plantilla-evidencias";
    }

    @PostMapping("/cargar")
    public String cargar(
            @RequestParam("archivo")
            MultipartFile archivo,
            RedirectAttributes redirectAttributes) {

        try {

            service.guardar(
                    archivo
            );

            redirectAttributes.addFlashAttribute(
                    "exito",
                    "La nueva plantilla quedó activa y reemplazó la plantilla anterior."
            );

        } catch (
                IllegalArgumentException
                | IllegalStateException exception) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    exception.getMessage()
            );
        }

        return "redirect:/administracion/configuracion/plantilla-evidencias";
    }

    @GetMapping("/descargar")
    public ResponseEntity<FileSystemResource>
    descargar() {

        Path plantilla =
                service
                        .obtenerPlantillaActiva()
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "No existe una plantilla activa."
                                )
                        );

        FileSystemResource recurso =
                new FileSystemResource(
                        plantilla
                );

        String nombre =
                plantilla
                        .getFileName()
                        .toString();

        MediaType tipo =
                nombre
                        .toLowerCase()
                        .endsWith(".pdf")
                        ? MediaType.APPLICATION_PDF
                        : MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                        );

        ContentDisposition disposition =
                ContentDisposition
                        .attachment()
                        .filename(
                                nombre,
                                StandardCharsets.UTF_8
                        )
                        .build();

        return ResponseEntity
                .ok()
                .contentType(
                        tipo
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        disposition.toString()
                )
                .body(
                        recurso
                );
    }

    private void cargarDatos(
            Model model) {

        model.addAttribute(
                "existePlantilla",
                service.existePlantillaActiva()
        );

        model.addAttribute(
                "nombrePlantilla",
                service.obtenerNombrePlantillaActiva()
        );

        model.addAttribute(
                "formatoPlantilla",
                service.obtenerFormatoPlantillaActiva()
        );

        model.addAttribute(
                "fechaPlantilla",
                service.obtenerFechaPlantillaActiva()
        );
    }
}