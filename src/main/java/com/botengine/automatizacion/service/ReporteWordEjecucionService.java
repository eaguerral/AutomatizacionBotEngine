package com.botengine.automatizacion.service;

import com.botengine.automatizacion.model.Automatizacion;
import com.botengine.automatizacion.model.Empresa;
import com.botengine.automatizacion.model.ResultadoEjecucion;
import com.botengine.automatizacion.repository.EmpresaRepository;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReporteWordEjecucionService {

    private static final String FUENTE_REPORTE =
            "system-ui";

    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm:ss"
            );

    private final EmpresaRepository empresaRepository;

    public ReporteWordEjecucionService(
            EmpresaRepository empresaRepository) {

        this.empresaRepository =
                empresaRepository;
    }

    public void generar(
            ResultadoEjecucion resultado) {

        if (resultado == null
                || resultado.getRutaReporteWord() == null
                || resultado.getRutaReporteWord().isBlank()) {

            return;
        }

        Path archivo =
                Path.of(
                        resultado.getRutaReporteWord()
                )
                .toAbsolutePath()
                .normalize();

        if (!Files.exists(archivo)) {

            throw new IllegalStateException(
                    "No existe el reporte Word preparado: "
                    + archivo
            );
        }

        try {

            XWPFDocument documento;

            try (InputStream entrada =
                         Files.newInputStream(
                                 archivo
                         )) {

                documento =
                        new XWPFDocument(
                                entrada
                        );
            }

            agregarSaltoPagina(
                    documento
            );

            agregarTitulo(
                    documento,
                    "REPORTE DE EJECUCIÓN AUTOMATIZADA"
            );

            agregarSubtitulo(
                    documento,
                    resultado
                            .getAutomatizacion()
                            .getNombre()
            );

            agregarEncabezado(
                    documento,
                    "1. Evidencia paso a paso"
            );

            agregarPasos(
                    documento,
                    resultado
            );

            agregarEncabezado(
                    documento,
                    "2. Resumen de la ejecución"
            );

            agregarResumen(
                    documento,
                    resultado
            );

            agregarEncabezado(
                    documento,
                    "3. Resultado final"
            );

            agregarResultadoFinal(
                    documento,
                    resultado
            );

            try (OutputStream salida =
                         Files.newOutputStream(
                                 archivo
                         )) {

                documento.write(
                        salida
                );
            }

            documento.close();

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "No fue posible generar el reporte Word.",
                    exception
            );
        }
    }

    private void agregarTitulo(
            XWPFDocument documento,
            String texto) {

        XWPFParagraph parrafo =
                documento.createParagraph();

        parrafo.setAlignment(
                ParagraphAlignment.CENTER
        );

        XWPFRun run =
                parrafo.createRun();

        configurarRun(
                run,
                16,
                true
        );

        run.setText(
                texto
        );
    }

    private void agregarSubtitulo(
            XWPFDocument documento,
            String texto) {

        XWPFParagraph parrafo =
                documento.createParagraph();

        parrafo.setAlignment(
                ParagraphAlignment.CENTER
        );

        XWPFRun run =
                parrafo.createRun();

        configurarRun(
                run,
                13,
                true
        );

        run.setText(
                texto
        );
    }

    private void agregarEncabezado(
            XWPFDocument documento,
            String texto) {

        XWPFParagraph parrafo =
                documento.createParagraph();

        parrafo.setAlignment(
                ParagraphAlignment.LEFT
        );

        XWPFRun run =
                parrafo.createRun();

        configurarRun(
                run,
                13,
                true
        );

        run.setText(
                texto
        );
    }

    private void agregarPasos(
            XWPFDocument documento,
            ResultadoEjecucion resultado) {

        List<PasoReporte> pasos =
                leerPasos(
                        resultado
                );

        if (pasos.isEmpty()) {

            agregarTexto(
                    documento,
                    "No se registraron evidencias visuales para esta ejecución."
            );

            return;
        }

        for (PasoReporte paso : pasos) {

            XWPFParagraph tituloPaso =
                    documento.createParagraph();

            tituloPaso.setAlignment(
                    ParagraphAlignment.LEFT
            );

            XWPFRun runTitulo =
                    tituloPaso.createRun();

            configurarRun(
                    runTitulo,
                    11,
                    true
            );

            runTitulo.setText(
                    "Paso "
                    + paso.numero()
                    + ". "
                    + paso.titulo()
            );

            Path imagen =
                    Path.of(
                            resultado.getRutaCapturas()
                    )
                    .toAbsolutePath()
                    .normalize()
                    .resolve(
                            paso.archivo()
                    );

            if (!Files.exists(imagen)) {
                continue;
            }

            XWPFParagraph parrafoImagen =
                    documento.createParagraph();

            parrafoImagen.setAlignment(
                    ParagraphAlignment.CENTER
            );

            XWPFRun runImagen =
                    parrafoImagen.createRun();

            try (InputStream entrada =
                         Files.newInputStream(
                                 imagen
                         )) {

                runImagen.addPicture(
                        entrada,
                        org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_PNG,
                        "Evidencia funcional",
                        Units.toEMU(
                                560
                        ),
                        Units.toEMU(
                                315
                        )
                );

            } catch (Exception exception) {

                agregarTexto(
                        documento,
                        "La evidencia visual de este paso no pudo incorporarse al documento."
                );
            }
        }
    }

    private List<PasoReporte> leerPasos(
            ResultadoEjecucion resultado) {

        List<PasoReporte> pasos =
                new ArrayList<>();

        if (resultado.getRutaCapturas() == null
                || resultado.getRutaCapturas().isBlank()) {

            return pasos;
        }

        Path carpeta =
                Path.of(
                        resultado.getRutaCapturas()
                )
                .toAbsolutePath()
                .normalize();

        Path manifiesto =
                carpeta.resolve(
                        "pasos.tsv"
                );

        if (!Files.exists(manifiesto)) {

            return pasos;
        }

        try {

            for (String linea :
                    Files.readAllLines(
                            manifiesto
                    )) {

                if (linea == null
                        || linea.isBlank()) {

                    continue;
                }

                String[] partes =
                        linea.split(
                                "\\t",
                                3
                        );

                if (partes.length != 3) {
                    continue;
                }

                int numero =
                        Integer.parseInt(
                                partes[0]
                        );

                pasos.add(
                        new PasoReporte(
                                numero,
                                partes[1],
                                partes[2]
                        )
                );
            }

        } catch (Exception exception) {

            return new ArrayList<>();
        }

        return pasos;
    }

    private void agregarResumen(
            XWPFDocument documento,
            ResultadoEjecucion resultado) {

        Automatizacion automatizacion =
                resultado.getAutomatizacion();

        XWPFTable tabla =
                documento.createTable(
                        8,
                        2
                );

        escribirFila(
                tabla,
                0,
                "Empresa",
                obtenerNombreEmpresa(
                        automatizacion.getSitioCodigo()
                )
        );

        escribirFila(
                tabla,
                1,
                "Automatización",
                automatizacion.getNombre()
        );

        escribirFila(
                tabla,
                2,
                "Transacción",
                resultado.getUuid()
        );

        escribirFila(
                tabla,
                3,
                "Usuario ejecutor",
                resultado
                        .getUsuario()
                        .getUsername()
        );

        escribirFila(
                tabla,
                4,
                "Fecha de inicio",
                formatearFecha(
                        resultado.getFechaInicio()
                )
        );

        escribirFila(
                tabla,
                5,
                "Fecha de finalización",
                formatearFecha(
                        resultado.getFechaFin()
                )
        );

        escribirFila(
                tabla,
                6,
                "Duración",
                resultado.getDuracionMilisegundos() == null
                        ? "No disponible"
                        : resultado.getDuracionMilisegundos()
                        + " ms"
        );

        escribirFila(
                tabla,
                7,
                "Resultado",
                resultado
                        .getEstado()
                        .name()
        );
    }

    private void agregarResultadoFinal(
            XWPFDocument documento,
            ResultadoEjecucion resultado) {

        String estado =
                resultado
                        .getEstado()
                        .name();

        agregarTextoNegrita(
                documento,
                "Resultado de la prueba: "
                + estado
        );

        String detalle =
                resultado.getDetalle();

        if (detalle != null
                && !detalle.isBlank()) {

            agregarTexto(
                    documento,
                    detalle
            );
        }

        agregarTexto(
                documento,
                "La ejecución y sus evidencias quedaron registradas con el identificador de transacción "
                + resultado.getUuid()
                + "."
        );
    }

    private void escribirFila(
            XWPFTable tabla,
            int fila,
            String etiqueta,
            String valor) {

        XWPFTableCell celdaEtiqueta =
                tabla
                        .getRow(
                                fila
                        )
                        .getCell(
                                0
                        );

        XWPFTableCell celdaValor =
                tabla
                        .getRow(
                                fila
                        )
                        .getCell(
                                1
                        );

        celdaEtiqueta.setText(
                etiqueta
        );

        celdaValor.setText(
                valor == null
                        ? ""
                        : valor
        );

        aplicarFuenteCelda(
                celdaEtiqueta,
                true
        );

        aplicarFuenteCelda(
                celdaValor,
                false
        );
    }

    private void aplicarFuenteCelda(
            XWPFTableCell celda,
            boolean negrita) {

        celda
                .getParagraphs()
                .forEach(
                        parrafo -> {

                            parrafo.setAlignment(
                                    ParagraphAlignment.LEFT
                            );

                            parrafo
                                    .getRuns()
                                    .forEach(
                                            run ->
                                                    configurarRun(
                                                            run,
                                                            10,
                                                            negrita
                                                    )
                                    );
                        }
                );
    }

    private void agregarTexto(
            XWPFDocument documento,
            String texto) {

        XWPFParagraph parrafo =
                documento.createParagraph();

        parrafo.setAlignment(
                ParagraphAlignment.LEFT
        );

        XWPFRun run =
                parrafo.createRun();

        configurarRun(
                run,
                11,
                false
        );

        run.setText(
                texto
        );
    }

    private void agregarTextoNegrita(
            XWPFDocument documento,
            String texto) {

        XWPFParagraph parrafo =
                documento.createParagraph();

        XWPFRun run =
                parrafo.createRun();

        configurarRun(
                run,
                11,
                true
        );

        run.setText(
                texto
        );
    }

    private void configurarRun(
            XWPFRun run,
            int tamano,
            boolean negrita) {

        run.setFontFamily(
                FUENTE_REPORTE
        );

        run.setFontSize(
                tamano
        );

        run.setBold(
                negrita
        );
    }

    private void agregarSaltoPagina(
            XWPFDocument documento) {

        if (!documento
                .getParagraphs()
                .isEmpty()) {

            XWPFParagraph parrafo =
                    documento.createParagraph();

            parrafo.setPageBreak(
                    true
            );
        }
    }

    private String obtenerNombreEmpresa(
            String codigo) {

        if (codigo == null
                || codigo.isBlank()) {

            return "No definida";
        }

        return empresaRepository
                .findByCodigo(
                        codigo
                )
                .map(
                        Empresa::getNombre
                )
                .orElse(
                        codigo
                );
    }

    private String formatearFecha(
            java.time.LocalDateTime fecha) {

        return fecha == null
                ? "No disponible"
                : fecha.format(
                        FECHA
                );
    }

    private record PasoReporte(
            int numero,
            String titulo,
            String archivo) {
    }
}