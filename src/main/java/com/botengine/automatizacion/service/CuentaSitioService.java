package com.botengine.automatizacion.service;

import com.botengine.automatizacion.dto.CredencialSitioForm;
import com.botengine.automatizacion.model.CuentaSitio;
import com.botengine.automatizacion.model.Empresa;
import com.botengine.automatizacion.repository.CuentaSitioRepository;
import com.botengine.automatizacion.repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class CuentaSitioService {

    private final CuentaSitioRepository repository;
    private final EmpresaRepository empresaRepository;
    private final CredencialCryptoService cryptoService;

    public CuentaSitioService(
            CuentaSitioRepository repository,
            EmpresaRepository empresaRepository,
            CredencialCryptoService cryptoService) {

        this.repository = repository;
        this.empresaRepository = empresaRepository;
        this.cryptoService = cryptoService;
    }

    public List<CuentaSitio> listarTodas() {

        return repository
                .findAllByOrderBySitioCodigoAscPrioridadAscIdAsc();
    }

    public CuentaSitio buscarPorId(
            Long id) {

        return repository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La credencial no existe."
                        )
                );
    }

    public CredencialSitioForm obtenerFormulario(
            Long id) {

        CuentaSitio cuenta =
                buscarPorId(id);

        Empresa empresa =
                empresaRepository
                        .findByCodigo(
                                cuenta.getSitioCodigo()
                        )
                        .orElse(null);

        CredencialSitioForm form =
                new CredencialSitioForm();

        form.setId(
                cuenta.getId()
        );

        if (empresa != null) {

            form.setEmpresaId(
                    empresa.getId()
            );
        }

        form.setAlias(
                cuenta.getAlias()
        );

        form.setUrl(
                cuenta.getUrl()
        );

        form.setPrioridad(
                cuenta.getPrioridad()
        );

        form.setActivo(
                cuenta.isActivo()
        );

        form.setUsuarioConfigurado(
                cuenta.getUsuarioCifrado() != null
                && !cuenta.getUsuarioCifrado().isBlank()
        );

        form.setPasswordConfigurado(
                cuenta.getPasswordCifrado() != null
                && !cuenta.getPasswordCifrado().isBlank()
        );

        return form;
    }

    @Transactional
    public CuentaSitio guardar(
            CredencialSitioForm form) {

        if (form.getEmpresaId() == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar una empresa."
            );
        }

        Empresa empresa =
                empresaRepository
                        .findById(
                                form.getEmpresaId()
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

        String sitioCodigo =
                normalizar(
                        empresa.getCodigo()
                );

        String alias =
                normalizar(
                        obligatorio(
                                form.getAlias(),
                                "El alias es obligatorio."
                        )
                );

        String url =
                obligatorio(
                        form.getUrl(),
                        "La URL es obligatoria."
                );

        validarUrl(
                url
        );

        Integer prioridad =
                form.getPrioridad();

        if (prioridad == null
                || prioridad < 1) {

            throw new IllegalArgumentException(
                    "La prioridad debe ser mayor o igual a 1."
            );
        }

        CuentaSitio cuenta;

        if (form.getId() == null) {

            if (repository
                    .existsBySitioCodigoAndAlias(
                            sitioCodigo,
                            alias
                    )) {

                throw new IllegalArgumentException(
                        "Ya existe una credencial con ese alias para la empresa."
                );
            }

            String username =
                    obligatorio(
                            form.getUsername(),
                            "El usuario es obligatorio."
                    );

            String password =
                    obligatorio(
                            form.getPassword(),
                            "La contraseña es obligatoria."
                    );

            cuenta =
                    new CuentaSitio();

            cuenta.setUsuarioCifrado(
                    cryptoService.cifrar(
                            username
                    )
            );

            cuenta.setPasswordCifrado(
                    cryptoService.cifrar(
                            password
                    )
            );

        } else {

            cuenta =
                    buscarPorId(
                            form.getId()
                    );

            if (repository
                    .existsBySitioCodigoAndAliasAndIdNot(
                            sitioCodigo,
                            alias,
                            cuenta.getId()
                    )) {

                throw new IllegalArgumentException(
                        "Ya existe otra credencial con ese alias para la empresa."
                );
            }

            if (tieneValor(
                    form.getUsername()
            )) {

                cuenta.setUsuarioCifrado(
                        cryptoService.cifrar(
                                form.getUsername().trim()
                        )
                );
            }

            if (tieneValor(
                    form.getPassword()
            )) {

                cuenta.setPasswordCifrado(
                        cryptoService.cifrar(
                                form.getPassword()
                        )
                );
            }
        }

        cuenta.setSitioCodigo(
                sitioCodigo
        );

        cuenta.setAlias(
                alias
        );

        cuenta.setUrl(
                url.trim()
        );

        cuenta.setPrioridad(
                prioridad
        );

        cuenta.setActivo(
                form.isActivo()
        );

        return repository.save(
                cuenta
        );
    }

    @Transactional
    public void cambiarEstado(
            Long id) {

        CuentaSitio cuenta =
                buscarPorId(id);

        cuenta.setActivo(
                !cuenta.isActivo()
        );

        repository.save(
                cuenta
        );
    }

    private String obligatorio(
            String valor,
            String mensaje) {

        if (!tieneValor(valor)) {

            throw new IllegalArgumentException(
                    mensaje
            );
        }

        return valor.trim();
    }

    private String normalizar(
            String valor) {

        return obligatorio(
                valor,
                "El valor no puede estar vacío."
        )
                .replace("-", "_")
                .replace(" ", "_")
                .toUpperCase(
                        Locale.ROOT
                );
    }

    private void validarUrl(
            String url) {

        String valor =
                url.toLowerCase(
                        Locale.ROOT
                );

        if (!valor.startsWith("http://")
                && !valor.startsWith("https://")) {

            throw new IllegalArgumentException(
                    "La URL debe iniciar con http:// o https://"
            );
        }
    }

    private boolean tieneValor(
            String valor) {

        return valor != null
                && !valor.isBlank();
    }
}