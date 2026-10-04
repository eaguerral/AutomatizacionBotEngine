package com.botengine.automatizacion.service;

import com.botengine.automatizacion.model.Empresa;
import com.botengine.automatizacion.model.ResultadoEjecucion;
import com.botengine.automatizacion.repository.EmpresaRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportePdfEjecucionService {

    private final EmpresaRepository empresaRepository;

    public ReportePdfEjecucionService(
            EmpresaRepository empresaRepository) {

        this.empresaRepository =
                empresaRepository;
    }

    public void generar(
            ResultadoEjecucion resultado) {

        if (resultado == null
                || resultado.getRutaCapturas() == null) {

            return;
        }

        try {

            Path carpetaCapturas =
                    Path.of(
                            resultado.getRutaCapturas()
                    )
                    .toAbsolutePath()
                    .normalize();

            Path carpetaEjecucion =
                    carpetaCapturas.getParent();

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

            PDDocument documento;

            if (Files.exists(destino)) {

                documento =
                        Loader.loadPDF(
                                destino.toFile()
                        );

            } else {

                documento =
                        new PDDocument();
            }

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
                            documento
                    );

            pagina =
                    texto(
                            documento,
                            pagina,
                            negrita,
                            16,
                            "REPORTE DE EJECUCION AUTOMATIZADA"
                    );

            pagina =
                    texto(
                            documento,
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
                            pagina,
                            negrita,
                            13,
                            "1. Evidencia paso a paso"
                    );

            List<Paso> pasos =
                    leerPasos(
                            carpetaCapturas
                    );

            for (Paso paso : pasos) {

                pagina =
                        texto(
                                documento,
                                pagina,
                                negrita,
                                11,
                                "Paso "
                                + paso.numero()
                                + ". "
                                + paso.titulo()
                        );

                if (!paso.descripcion().isBlank()) {

                    pagina =
                            texto(
                                    documento,
                                    pagina,
                                    normal,
                                    10,
                                    paso.descripcion()
                            );
                }

                Path imagen =
                        carpetaCapturas.resolve(
                                paso.archivo()
                        );

                if (Files.exists(imagen)) {

                    PDImageXObject pdfImagen =
                            PDImageXObject
                                    .createFromFileByContent(
                                            imagen.toFile(),
                                            documento
                                    );

                    float maxAncho =
                            500F;

                    float maxAlto =
                            280F;

                    float escala =
                            Math.min(
                                    maxAncho
                                    / pdfImagen.getWidth(),
                                    maxAlto
                                    / pdfImagen.getHeight()
                            );

                    float ancho =
                            pdfImagen.getWidth()
                            * escala;

                    float alto =
                            pdfImagen.getHeight()
                            * escala;

                    if (pagina.y - alto < 60F) {

                        pagina.cerrar();

                        pagina =
                                nuevaPagina(
                                        documento
                                );
                    }

                    pagina.contenido.drawImage(
                            pdfImagen,
                            55F,
                            pagina.y - alto,
                            ancho,
                            alto
                    );

                    pagina.y -=
                            alto
                            + 22F;
                }
            }

            if (pagina.y < 240F) {

                pagina.cerrar();

                pagina =
                        nuevaPagina(
                                documento
                        );
            }

            pagina =
                    texto(
                            documento,
                            pagina,
                            negrita,
                            13,
                            "2. Resumen de la ejecucion"
                    );

            pagina =
                    texto(
                            documento,
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
                            pagina,
                            normal,
                            10,
                            "Transaccion: "
                            + resultado.getUuid()
                    );

            pagina =
                    texto(
                            documento,
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
                            pagina,
                            normal,
                            10,
                            "Duracion: "
                            + resultado.getDuracionMilisegundos()
                            + " ms"
                    );

            pagina =
                    texto(
                            documento,
                            pagina,
                            negrita,
                            13,
                            "3. Resultado final"
                    );

            pagina =
                    texto(
                            documento,
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

            documento.close();

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
            PDDocument documento)
            throws Exception {

        PDPage pagina =
                new PDPage(
                        PDRectangle.LETTER
                );

        documento.addPage(
                pagina
        );

        return new Pagina(
                new PDPageContentStream(
                        documento,
                        pagina
                ),
                740F
        );
    }

    private Pagina texto(
            PDDocument documento,
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

            if (pagina.y < 60F) {

                pagina.cerrar();

                pagina =
                        nuevaPagina(
                                documento
                        );
            }

            pagina.contenido.beginText();

            pagina.contenido.setFont(
                    fuente,
                    tamano
            );

            pagina.contenido.newLineAtOffset(
                    55F,
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

        pagina.y -= 5F;

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
                corte = maximo;
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
        private float y;

        private Pagina(
                PDPageContentStream contenido,
                float y) {

            this.contenido =
                    contenido;

            this.y =
                    y;
        }

        private void cerrar()
                throws Exception {

            contenido.close();
        }
    }
}