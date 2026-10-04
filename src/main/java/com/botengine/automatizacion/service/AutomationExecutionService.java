package com.botengine.automatizacion.service;

import com.botengine.framework.core.EjecucionContexto;
import com.botengine.automatizacion.model.Automatizacion;
import com.botengine.automatizacion.model.EstadoEjecucion;
import com.botengine.automatizacion.model.ResultadoEjecucion;
import com.botengine.automatizacion.model.Usuario;
import com.botengine.automatizacion.repository.ResultadoEjecucionRepository;
import com.botengine.framework.utils.ScreenshotListener;
import com.botengine.framework.utils.EvidenciaPaso;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AutomationExecutionService {

    private final ResultadoEjecucionRepository
            resultadoRepository;

    private final EvidenciaEjecucionService
            evidenciaEjecucionService;

    private final DocumentacionEjecucionService
            documentacionEjecucionService;

    public AutomationExecutionService(
            ResultadoEjecucionRepository resultadoRepository,
            EvidenciaEjecucionService evidenciaEjecucionService,
            DocumentacionEjecucionService documentacionEjecucionService) {

        this.resultadoRepository =
                resultadoRepository;

        this.evidenciaEjecucionService =
                evidenciaEjecucionService;

        this.documentacionEjecucionService =
                documentacionEjecucionService;
    }

    public ResultadoEjecucion ejecutar(
            Automatizacion automatizacion,
            Usuario usuario) {

        if (automatizacion == null) {

            throw new IllegalArgumentException(
                    "La automatización no existe."
            );
        }

        if (usuario == null) {

            throw new IllegalArgumentException(
                    "El usuario no existe."
            );
        }

        if (!automatizacion.isActivo()) {

            throw new IllegalArgumentException(
                    "La automatización se encuentra inactiva."
            );
        }

        /*
         * PASO 1:
         * Crear la transacción de ejecución.
         *
         * Al persistirla PostgreSQL genera el UUID que será
         * utilizado para identificar todas sus evidencias.
         */
        ResultadoEjecucion resultado =
                new ResultadoEjecucion();

        resultado.setUsuario(
                usuario
        );

        resultado.setAutomatizacion(
                automatizacion
        );

        resultado.setEstado(
                EstadoEjecucion.PENDIENTE
        );

        resultado =
                resultadoRepository
                        .saveAndFlush(
                                resultado
                        );

        LocalDateTime inicio =
                LocalDateTime.now();

        resultado.setFechaInicio(
                inicio
        );

        resultado.setEstado(
                EstadoEjecucion.EN_EJECUCION
        );

        resultado =
                resultadoRepository
                        .saveAndFlush(
                                resultado
                        );

        TestListenerAdapter listener =
                new TestListenerAdapter();

        Path xmlTemporal = null;

        try {

            /*
             * PASO 2:
             * Preparar automáticamente la carpeta de evidencias
             * y copiar la plantilla activa con un nombre único
             * asociado a esta transacción.
             */
            evidenciaEjecucionService.preparar(
                    resultado
            );

            resultado =
                    resultadoRepository
                            .saveAndFlush(
                                    resultado
                            );

            /*
             * PASO 3:
             * Preparar TestNG.
             */
            /*
             * Activar el registro de evidencias paso a paso
             * para la transaccion actual.
             */
            EjecucionContexto.iniciar(
                    resultado.getUuid(),
                    resultado.getNombreBaseReporte()
            );

            EvidenciaPaso.iniciar(
                    resultado.getRutaCapturas()
            );

            TestNG testNG =
                    new TestNG();

            testNG.addListener(
                    listener
            );

            /*
             * Cada ejecucion entrega al listener su propia
             * carpeta de capturas, asociada al UUID.
             */
            testNG.addListener(
                    new ScreenshotListener(
                            resultado.getRutaCapturas()
                    )
            );

            testNG.setUseDefaultListeners(
                    true
            );

            testNG.setOutputDirectory(
                    "reportes/testng/portal"
            );

            String archivoXml =
                    automatizacion
                            .getArchivoXml();

            /*
             * PASO 4:
             * Si existe XML asociado, ejecutar el suite
             * dinámicamente desde el classpath.
             */
            if (archivoXml != null
                    && !archivoXml.isBlank()) {

                ClassPathResource recurso =
                        new ClassPathResource(
                                archivoXml
                        );

                if (!recurso.exists()) {

                    throw new IllegalArgumentException(
                            "No existe el XML de ejecución: "
                            + archivoXml
                    );
                }

                xmlTemporal =
                        Files.createTempFile(
                                "botengine-testng-",
                                ".xml"
                        );

                try (InputStream input =
                             recurso.getInputStream()) {

                    Files.copy(
                            input,
                            xmlTemporal,
                            StandardCopyOption.REPLACE_EXISTING
                    );
                }

                testNG.setTestSuites(
                        List.of(
                                xmlTemporal.toString()
                        )
                );

            } else {

                /*
                 * Si no existe XML, ejecutar directamente
                 * la clase TestNG publicada.
                 */
                Class<?> claseTest =
                        Class.forName(
                                automatizacion
                                        .getClaseTest()
                        );

                testNG.setTestClasses(
                        new Class[]{
                                claseTest
                        }
                );
            }

            /*
             * PASO 5:
             * Ejecutar la automatización.
             */
            testNG.run();

            boolean ejecucionConError =
                    !listener
                            .getFailedTests()
                            .isEmpty()
                    ||
                    !listener
                            .getSkippedTests()
                            .isEmpty();

            if (ejecucionConError) {

                resultado.setEstado(
                        EstadoEjecucion.FALLIDO
                );

                resultado.setDetalle(
                        "La ejecución finalizó con pruebas fallidas o omitidas."
                );

            } else {

                resultado.setEstado(
                        EstadoEjecucion.EXITOSO
                );

                resultado.setDetalle(
                        "La ejecución finalizó correctamente."
                );
            }

        } catch (Exception exception) {

            resultado.setEstado(
                    EstadoEjecucion.FALLIDO
            );

            String mensaje =
                    exception.getMessage();

            if (mensaje == null
                    || mensaje.isBlank()) {

                mensaje =
                        exception
                                .getClass()
                                .getSimpleName();
            }

            if (mensaje.length() > 450) {

                mensaje =
                        mensaje.substring(
                                0,
                                450
                        );
            }

            resultado.setDetalle(
                    "Error durante la ejecución: "
                    + mensaje
            );

        } finally {

            EvidenciaPaso.finalizar();
            EjecucionContexto.finalizar();

            if (xmlTemporal != null) {

                try {

                    Files.deleteIfExists(
                            xmlTemporal
                    );

                } catch (Exception ignored) {

                    /*
                     * La eliminación del XML temporal no debe
                     * modificar el resultado funcional.
                     */
                }
            }
        }

        LocalDateTime fin =
                LocalDateTime.now();

        resultado.setFechaFin(
                fin
        );

        resultado.setDuracionMilisegundos(
                Duration.between(
                        inicio,
                        fin
                ).toMillis()
        );

        /*
         * Se persiste primero el resultado funcional definitivo.
         * La generacion documental no debe alterar el estado
         * EXITOSO o FALLIDO de la prueba.
         */
        resultado =
                resultadoRepository
                        .saveAndFlush(
                                resultado
                        );

        try {

            /*
             * Generar el documento Word utilizando la plantilla
             * activa y las capturas correspondientes al UUID.
             */
            documentacionEjecucionService.generar(
                    resultado
            );

        } catch (Exception exception) {

            /*
             * Un problema documental no debe convertir una
             * ejecucion funcional exitosa en fallida.
             */
            String detalleActual =
                    resultado.getDetalle();

            String advertencia =
                    " No fue posible generar la documentacion de la ejecucion.";

            String detalleFinal =
                    (detalleActual == null
                            ? ""
                            : detalleActual)
                    + advertencia;

            if (detalleFinal.length() > 500) {

                detalleFinal =
                        detalleFinal.substring(
                                0,
                                500
                        );
            }

            resultado.setDetalle(
                    detalleFinal
            );

            resultado =
                    resultadoRepository
                            .saveAndFlush(
                                    resultado
                            );
        }

        return resultado;
    }
}