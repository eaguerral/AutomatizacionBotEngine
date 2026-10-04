package com.botengine.automatizacion.service;

import com.botengine.automatizacion.dto.CredencialSitio;
import com.botengine.automatizacion.model.CuentaSitio;
import com.botengine.automatizacion.repository.CuentaSitioRepository;
import com.botengine.framework.utils.ConfigReader;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class CredencialSitioService {

    private final CuentaSitioRepository
            cuentaSitioRepository;

    private final CredencialCryptoService
            cryptoService;

    public CredencialSitioService(
            CuentaSitioRepository cuentaSitioRepository,
            CredencialCryptoService cryptoService) {

        this.cuentaSitioRepository =
                cuentaSitioRepository;

        this.cryptoService =
                cryptoService;
    }

    public CredencialSitio obtenerCredencialDisponible(
            String sitioCodigo) {

        String sitio =
                normalizar(
                        sitioCodigo
                );

        String urlEntorno =
                ConfigReader.getEnvOptional(
                        sitio + "_URL"
                );

        List<CuentaSitio> cuentas =
                cuentaSitioRepository
                        .findBySitioCodigoAndActivoTrueOrderByPrioridadAscIdAsc(
                                sitio
                        );

        for (CuentaSitio cuenta : cuentas) {

            String alias =
                    normalizar(
                            cuenta.getAlias()
                    );

            String url =
                    tieneValor(
                            cuenta.getUrl()
                    )
                            ? cuenta.getUrl().trim()
                            : urlEntorno;

            if (!tieneValor(url)) {
                continue;
            }

            boolean credencialBaseDatos =
                    tieneValor(
                            cuenta.getUsuarioCifrado()
                    )
                    && tieneValor(
                            cuenta.getPasswordCifrado()
                    );

            if (credencialBaseDatos) {

                return new CredencialSitio(
                        sitio,
                        alias,
                        url,
                        cryptoService.descifrar(
                                cuenta.getUsuarioCifrado()
                        ),
                        cryptoService.descifrar(
                                cuenta.getPasswordCifrado()
                        )
                );
            }

            String username =
                    ConfigReader.getEnvOptional(
                            alias + "_USERNAME"
                    );

            String password =
                    ConfigReader.getEnvOptional(
                            alias + "_PASSWORD"
                    );

            if (tieneValor(username)
                    && tieneValor(password)) {

                return new CredencialSitio(
                        sitio,
                        alias,
                        url,
                        username,
                        password
                );
            }
        }

        throw new IllegalStateException(
                "No existe una cuenta activa con credenciales completas para el sitio "
                + sitio
        );
    }

    private String normalizar(
            String valor) {

        if (valor == null
                || valor.isBlank()) {

            throw new IllegalArgumentException(
                    "El código o alias no puede estar vacío."
            );
        }

        return valor
                .trim()
                .replace("-", "_")
                .replace(" ", "_")
                .toUpperCase(
                        Locale.ROOT
                );
    }

    private boolean tieneValor(
            String valor) {

        return valor != null
                && !valor.isBlank();
    }
}