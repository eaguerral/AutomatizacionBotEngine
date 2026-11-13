package com.botengine.automatizacion.service;

import com.botengine.automatizacion.model.Empresa;
import com.botengine.automatizacion.model.ResultadoEjecucion;
import com.botengine.automatizacion.repository.EmpresaRepository;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReportePdfEjecucionService {

    private static final float MARGEN_IZQUIERDO =
            55F;

    private static final float MARGEN_INFERIOR =
            85F;

    private static final float MARGEN_SUPERIOR =
            90F;

    /*
     * Altura maxima de cada captura.
     *
     * Permite mostrar hasta dos evidencias completas
     * por pagina conservando titulo y descripcion.
     */
    private static final float ALTURA_MAXIMA_CAPTURA =
            200F;

    private static final float ESPACIO_DESPUES_CAPTURA =
            14F;

    private static final int EVIDENCIAS_POR_PAGINA =
            2;

    private final EmpresaRepository empresaRepository;

    private final PlantillaEvidenciaService
            plantillaEvidenciaService;

    public ReportePdfEjecucionService(
            EmpresaRepository empresaRepository,
            PlantillaEvidenciaService plantillaEvidenciaService) {

        this.empresaRepository =
                empresaRepository;

        this.plantillaEvidenciaService =
                plantillaEvidenciaService;
    }

    public void generar(
            ResultadoEjecucion resultado) {

        if (resultado == null
                || resultado.getRutaCapturas() == null
                || resultado.getRutaCapturas().isBlank()) {

            return;
        }

        PDDocument plantillaDocumento =
                null;

        try {

            Path carpetaCapturas =
                    Path.of(
                            resultado.getRutaCapturas()
                    )
                    .toAbsolutePath()
                    .normalize();

            Path carpetaEjecucion =
                    carpetaCapturas.getParent();

            if (carpetaEjecucion == null) {

                throw new IllegalStateException(
                        "No fue posible determinar la carpeta de la ejecucion."
                );
            }

            Files.createDirectories(
                    carpetaEjecucion
            );

            Path destino;

            if (resultado.getRutaReportePdf() != null
                    && !resultado.getRutaReportePdf().isBlank()) {

                destino =
                        Path.of(
                            resultado.getRutaReportePdf()
                        )
                        .toAbsolutePath()
                        .normalize();

            } else {

                destino =
                        carpetaEjecucion.resolve(
                                resultado.getNombreBaseReporte()
                                        + ".pdf"
                        );
            }

            /*
             * Obtener plantilla PDF activa.
             */
            Optional<Path> plantillaActiva =
                    plantillaEvidenciaService
                            .obtenerPlantillaActiva();

            if (plantillaActiva.isPresent()) {

                Path plantilla =
                        plantillaActiva.get();

                String nombre =
                        plantilla
                                .getFileName()
                                .toString()
                                .toLowerCase();

                if (nombre.endsWith(".pdf")) {

                    plantillaDocumento =
                            Loader.loadPDF(
                                    plantilla.toFile()
                            );
                }
            }

            try (PDDocument documento =
                         new PDDocument()) {

                PDType1Font normal =
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA
                        );

                PDType1Font negrita =
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA_BOLD
                        );

                Pagina pagina =
                        nuevaPagina(
                                documento,
                                plantillaDocumento
                        );

                /*
                 * Encabezado principal.
                 */
                pagina =
                        texto(
                                documento,
                                plantillaDocumento,
                                pagina,
                                negrita,
                                16,
                                "REPORTE DE EJECUCION AUTOMATIZADA"
                        );

                pagina =
                        texto(
                                documento,
                                plantillaDocumento,
                                pagina,
                                negrita,
                                13,
                                resultado
                                        .getAutomatizacion()
                                        .getNombre()
                        );

                pagina =
                        texto(
                                documento,
                                plantillaDocumento,
                                pagina,
                                negrita,
                                13,
                                "1. Evidencia paso a paso"
                        );

                List<Paso> pasos =
                        leerPasos(
                                carpetaCapturas
                        );

                int evidenciasEnPagina =
                        0;

                for (Paso paso : pasos) {

                    Path imagen =
                            carpetaCapturas.resolve(
                                    paso.archivo()
                            );

                    PDImageXObject pdfImagen =
                            null;

                    float anchoImagen =
                            0F;

                    float altoImagen =
                            0F;

                    /*
                     * Preparar dimensiones antes de escribir
                     * el titulo.
                     *
                     * De esta manera se evita dejar un titulo
                     * al final de una pagina sin su captura.
                     */
                    if (Files.exists(imagen)) {

                        pdfImagen =
                                PDImageXObject
                                        .createFromFileByContent(
                                                imagen.toFile(),
                                                documento
                                        );

                        float maxAncho =
                                pagina.anchoDisponible();

                        float escala =
                                Math.min(
                                        maxAncho
                                                / pdfImagen.getWidth(),
                                        ALTURA_MAXIMA_CAPTURA
                                                / pdfImagen.getHeight()
                                );

                        anchoImagen =
                                pdfImagen.getWidth()
                                        * escala;

                        altoImagen =
                                pdfImagen.getHeight()
                                        * escala;
                    }

                    List<String> descripcionLineas =
                            paso.descripcion().isBlank()
                                    ? List.of()
                                    : dividir(
                                            paso.descripcion(),
                                            88
                                    );

                    /*
                     * Calcular la altura aproximada del
                     * bloque completo:
                     *
                     * titulo
                     * descripcion
                     * captura
                     */
                    float alturaBloque =
                            23F;

                    if (!descripcionLineas.isEmpty()) {

                        alturaBloque +=
                                descripcionLineas.size()
                                        * 17F
                                        + 5F;
                    }

                    if (pdfImagen != null) {

                        alturaBloque +=
                                altoImagen
                                        + ESPACIO_DESPUES_CAPTURA;
                    }

                    /*
                     * Nueva pagina cuando:
                     *
                     * 1. Ya hay dos evidencias.
                     * 2. El bloque completo no cabe.
                     *
                     * Esto mantiene titulo, descripcion e
                     * imagen siempre juntos.
                     */
                    if (evidenciasEnPagina
                            >= EVIDENCIAS_POR_PAGINA
                            || pagina.y
                            - alturaBloque
                            < MARGEN_INFERIOR) {

                        pagina.cerrar();

                        pagina =
                                nuevaPagina(
                                        documento,
                                        plantillaDocumento
                                );

                        evidenciasEnPagina =
                                0;
                    }

                    /*
                     * Titulo del paso.
                     */
                    pagina =
                            texto(
                                    documento,
                                    plantillaDocumento,
                                    pagina,
                                    negrita,
                                    11,
                                    "Paso "
                                            + paso.numero()
                                            + ". "
                                            + paso.titulo()
                            );

                    /*
                     * Descripcion inmediatamente debajo
                     * del titulo.
                     */
                    if (!paso.descripcion().isBlank()) {

                        pagina =
                                texto(
                                        documento,
                                        plantillaDocumento,
                                        pagina,
                                        normal,
                                        10,
                                        paso.descripcion()
                                );
                    }

                    /*
                     * Captura inmediatamente debajo de
                     * titulo y descripcion.
                     */
                    if (pdfImagen != null) {

                        float x =
                                (pagina.anchoPagina()
                                        - anchoImagen)
                                        / 2F;

                        pagina.contenido.drawImage(
                                pdfImagen,
                                x,
                                pagina.y - altoImagen,
                                anchoImagen,
                                altoImagen
                        );

                        pagina.y -=
                                altoImagen
                                        + ESPACIO_DESPUES_CAPTURA;
                    }

                    evidenciasEnPagina++;
                }

                /*
                 * El resumen continúa inmediatamente
                 * despues de la ultima evidencia.
                 *
                 * Si no existe espacio suficiente,
                 * comienza una pagina nueva.
                 */
                if (pagina.y < 245F) {

                    pagina.cerrar();

                    pagina =
                            nuevaPagina(
                                    documento,
                                    plantillaDocumento
                            );
                }

                pagina =
                        texto(
                                documento,
                                plantillaDocumento,
                                pagina,
                                negrita,
                                13,
                                "2. Resumen de la ejecucion"
                        );

                pagina =
                        texto(
                                documento,
                                plantillaDocumento,
                                pagina,
                                normal,
                                10,
                                "Empresa: "
                                        + obtenerEmpresa(
                                                resultado
                                        )
                        );

                pagina =
                        texto(
                                documento,
                                plantillaDocumento,
                                pagina,
                                normal,
                                10,
                                "Automatizacion: "
                                        + resultado
                                                .getAutomatizacion()
                                                .getNombre()
                        );

                pagina =
                        texto(
                                documento,
                                plantillaDocumento,
                                pagina,
                                normal,
                                10,
                                "Transaccion: "
                                        + resultado.getUuid()
                        );

                pagina =
                        texto(
                                documento,
                                plantillaDocumento,
                                pagina,
                                normal,
                                10,
                                "Usuario ejecutor: "
                                        + resultado
                                                .getUsuario()
                                                .getUsername()
                        );

                pagina =
                        texto(
                                documento,
                                plantillaDocumento,
                                pagina,
                                normal,
                                10,
                                "Duracion: "
                                        + resultado
                                                .getDuracionMilisegundos()
                                        + " ms"
                        );

                pagina =
                        texto(
                                documento,
                                plantillaDocumento,
                                pagina,
                                negrita,
                                13,
                                "3. Resultado final"
                        );

                pagina =
                        texto(
                                documento,
                                plantillaDocumento,
                                pagina,
                                negrita,
                                11,
                                "Resultado de la prueba: "
                                        + resultado
                                                .getEstado()
                                                .name()
                        );

                pagina.cerrar();

                documento.save(
                        destino.toFile()
                );
            }

            resultado.setRutaReportePdf(
                    rutaPortable(
                            destino
                    )
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "No fue posible generar el PDF.",
                    exception
            );

        } finally {

            if (plantillaDocumento != null) {

                try {

                    plantillaDocumento.close();

                } catch (Exception ignored) {
                }
            }
        }
    }

    private List<Paso> leerPasos(
            Path carpeta) {

        List<Paso> pasos =
                new ArrayList<>();

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

                String[] partes =
                        linea.split(
                                "\\t",
                                4
                        );

                if (partes.length != 4) {

                    continue;
                }

                pasos.add(
                        new Paso(
                                Integer.parseInt(
                                        partes[0]
                                ),
                                partes[1],
                                partes[2],
                                partes[3]
                        )
                );
            }

        } catch (Exception ignored) {
        }

        return pasos;
    }

    private Pagina nuevaPagina(
            PDDocument documento,
            PDDocument plantillaDocumento)
            throws Exception {

        PDRectangle tamanoPagina;

        if (plantillaDocumento != null
                && plantillaDocumento
                        .getNumberOfPages() > 0) {

            PDPage paginaPlantilla =
                    plantillaDocumento
                            .getPage(
                                    0
                            );

            tamanoPagina =
                    new PDRectangle(
                            paginaPlantilla
                                    .getMediaBox()
                                    .getWidth(),
                            paginaPlantilla
                                    .getMediaBox()
                                    .getHeight()
                    );

        } else {

            tamanoPagina =
                    PDRectangle.LETTER;
        }

        PDPage pagina =
                new PDPage(
                        tamanoPagina
                );

        documento.addPage(
                pagina
        );

        if (plantillaDocumento != null
                && plantillaDocumento
                        .getNumberOfPages() > 0) {

            LayerUtility layerUtility =
                    new LayerUtility(
                            documento
                    );

            PDFormXObject fondo =
                    layerUtility
                            .importPageAsForm(
                                    plantillaDocumento,
                                    0
                            );

            try (PDPageContentStream fondoStream =
                         new PDPageContentStream(
                                 documento,
                                 pagina,
                                 PDPageContentStream.AppendMode.OVERWRITE,
                                 true,
                                 true
                         )) {

                fondoStream.drawForm(
                        fondo
                );
            }
        }

        PDPageContentStream contenido =
                new PDPageContentStream(
                        documento,
                        pagina,
                        PDPageContentStream.AppendMode.APPEND,
                        true,
                        true
                );

        float yInicial =
                tamanoPagina.getHeight()
                        - MARGEN_SUPERIOR;

        return new Pagina(
                contenido,
                yInicial,
                tamanoPagina.getWidth()
        );
    }

    private Pagina texto(
            PDDocument documento,
            PDDocument plantillaDocumento,
            Pagina pagina,
            PDType1Font fuente,
            float tamano,
            String texto)
            throws Exception {

        for (String linea :
                dividir(
                        texto,
                        88
                )) {

            if (pagina.y
                    < MARGEN_INFERIOR) {

                pagina.cerrar();

                pagina =
                        nuevaPagina(
                                documento,
                                plantillaDocumento
                        );
            }

            pagina.contenido.beginText();

            pagina.contenido.setFont(
                    fuente,
                    tamano
            );

            pagina.contenido.newLineAtOffset(
                    MARGEN_IZQUIERDO,
                    pagina.y
            );

            pagina.contenido.showText(
                    normalizarTexto(
                            linea
                    )
            );

            pagina.contenido.endText();

            pagina.y -=
                    tamano
                            + 7F;
        }

        pagina.y -=
                5F;

        return pagina;
    }

    private List<String> dividir(
            String texto,
            int maximo) {

        List<String> resultado =
                new ArrayList<>();

        if (texto == null) {

            resultado.add(
                    ""
            );

            return resultado;
        }

        String restante =
                texto.trim();

        while (restante.length() > maximo) {

            int corte =
                    restante.lastIndexOf(
                            ' ',
                            maximo
                    );

            if (corte <= 0) {

                corte =
                        maximo;
            }

            resultado.add(
                    restante
                            .substring(
                                    0,
                                    corte
                            )
                            .trim()
            );

            restante =
                    restante
                            .substring(
                                    corte
                            )
                            .trim();
        }

        resultado.add(
                restante
        );

        return resultado;
    }

    private String normalizarTexto(
            String texto) {

        if (texto == null) {

            return "";
        }

        return texto
                .replace(
                        "—",
                        "-"
                )
                .replace(
                        "–",
                        "-"
                );
    }

    private String obtenerEmpresa(
            ResultadoEjecucion resultado) {

        String codigo =
                resultado
                        .getAutomatizacion()
                        .getSitioCodigo();

        if (codigo == null) {

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

    private String rutaPortable(
            Path ruta) {

        Path proyecto =
                Path.of("")
                        .toAbsolutePath()
                        .normalize();

        try {

            return proyecto
                    .relativize(
                            ruta
                    )
                    .toString()
                    .replace(
                            "\\",
                            "/"
                    );

        } catch (Exception exception) {

            return ruta
                    .toString()
                    .replace(
                            "\\",
                            "/"
                    );
        }
    }

    private record Paso(
            int numero,
            String titulo,
            String descripcion,
            String archivo) {
    }

    private static final class Pagina {

        private final PDPageContentStream contenido;

        private final float anchoPagina;

        private float y;

        private Pagina(
                PDPageContentStream contenido,
                float y,
                float anchoPagina) {

            this.contenido =
                    contenido;

            this.y =
                    y;

            this.anchoPagina =
                    anchoPagina;
        }

        private float anchoPagina() {

            return anchoPagina;
        }

        private float anchoDisponible() {

            return anchoPagina
                    - (MARGEN_IZQUIERDO * 2F);
        }

        private void cerrar()
                throws Exception {

            contenido.close();
        }
    }
}