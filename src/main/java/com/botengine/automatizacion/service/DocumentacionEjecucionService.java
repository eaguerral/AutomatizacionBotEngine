package com.botengine.automatizacion.service;

import com.botengine.automatizacion.model.ResultadoEjecucion;
import com.botengine.automatizacion.repository.ResultadoEjecucionRepository;
import org.springframework.stereotype.Service;

/**
 * Genera y asocia la documentacion de una ejecucion.
 */
@Service
public class DocumentacionEjecucionService {

    private final ReporteWordEjecucionService wordService;

    private final ReportePdfEjecucionService pdfService;

    private final SeleniumVideoService videoService;

    private final ResultadoEjecucionRepository resultadoRepository;

    public DocumentacionEjecucionService(
            ReporteWordEjecucionService wordService,
            ReportePdfEjecucionService pdfService,
            SeleniumVideoService videoService,
            ResultadoEjecucionRepository resultadoRepository) {

        this.wordService =
                wordService;

        this.pdfService =
                pdfService;

        this.videoService =
                videoService;

        this.resultadoRepository =
                resultadoRepository;
    }

    public void generar(
            ResultadoEjecucion resultado) {

        try {

            wordService.generar(
                    resultado
            );

        } catch (Exception exception) {

            System.err.println(
                    "Advertencia Word: "
                    + exception.getMessage()
            );
        }

        try {

            pdfService.generar(
                    resultado
            );

        } catch (Exception exception) {

            System.err.println(
                    "Advertencia PDF: "
                    + exception.getMessage()
            );
        }

        try {

            /*
             * Recupera el video continuo generado por Selenium
             * una vez que driver.quit() cerro la sesion.
             */
            videoService.asociar(
                    resultado
            );

        } catch (Exception exception) {

            System.err.println(
                    "Advertencia video Selenium: "
                    + exception.getMessage()
            );
        }

        resultadoRepository.saveAndFlush(
                resultado
        );
    }
}