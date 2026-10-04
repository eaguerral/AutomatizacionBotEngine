package com.botengine.automatizacion.service;

import com.botengine.automatizacion.dto.EmpresaForm;
import com.botengine.automatizacion.model.Empresa;
import com.botengine.automatizacion.repository.EmpresaRepository;
import com.botengine.automatizacion.repository.InsumoAutomatizacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final InsumoAutomatizacionRepository insumoRepository;

    public EmpresaService(
            EmpresaRepository empresaRepository,
            InsumoAutomatizacionRepository insumoRepository) {

        this.empresaRepository = empresaRepository;
        this.insumoRepository = insumoRepository;
    }

    public List<Empresa> listarTodas() {

        return empresaRepository
                .findAllByOrderByNombreAsc();
    }

    public List<Empresa> listarActivas() {

        return empresaRepository
                .findByActivoTrueOrderByNombreAsc();
    }

    public Empresa buscarPorId(Long id) {

        return empresaRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La empresa no existe."
                        )
                );
    }

    public EmpresaForm obtenerFormulario(Long id) {

        Empresa empresa =
                buscarPorId(id);

        EmpresaForm form =
                new EmpresaForm();

        form.setId(empresa.getId());
        form.setCodigo(empresa.getCodigo());
        form.setNombre(empresa.getNombre());
        form.setCarpeta(empresa.getCarpeta());
        form.setDescripcion(empresa.getDescripcion());
        form.setActivo(empresa.isActivo());

        return form;
    }

    public Empresa guardar(EmpresaForm form) {

        String codigo =
                obligatorio(
                        form.getCodigo(),
                        "El código es obligatorio."
                )
                        .toUpperCase(Locale.ROOT)
                        .replace(" ", "_")
                        .replace("-", "_");

        String nombre =
                obligatorio(
                        form.getNombre(),
                        "El nombre es obligatorio."
                );

        String carpeta =
                obligatorio(
                        form.getCarpeta(),
                        "La carpeta es obligatoria."
                )
                        .toLowerCase(Locale.ROOT)
                        .replace(" ", "")
                        .replace("-", "")
                        .replaceAll("[^a-z0-9_]", "");

        Empresa empresa;

        if (form.getId() == null) {

            if (empresaRepository.existsByCodigo(codigo)) {
                throw new IllegalArgumentException(
                        "Ya existe una empresa con ese código."
                );
            }

            if (empresaRepository.existsByCarpeta(carpeta)) {
                throw new IllegalArgumentException(
                        "Ya existe una empresa con esa carpeta."
                );
            }

            empresa =
                    new Empresa();

        } else {

            empresa =
                    buscarPorId(
                            form.getId()
                    );

            if (empresaRepository.existsByCodigoAndIdNot(
                    codigo,
                    empresa.getId())) {

                throw new IllegalArgumentException(
                        "Ya existe otra empresa con ese código."
                );
            }

            if (empresaRepository.existsByCarpetaAndIdNot(
                    carpeta,
                    empresa.getId())) {

                throw new IllegalArgumentException(
                        "Ya existe otra empresa con esa carpeta."
                );
            }

            if (!empresa.getCarpeta().equals(carpeta)
                    && insumoRepository.existsByCarpetaEmpresa(
                            empresa.getCarpeta())) {

                throw new IllegalArgumentException(
                        "No se puede cambiar la carpeta porque ya tiene clases asociadas."
                );
            }
        }

        empresa.setCodigo(codigo);
        empresa.setNombre(nombre);
        empresa.setCarpeta(carpeta);
        empresa.setDescripcion(
                opcional(
                        form.getDescripcion()
                )
        );
        empresa.setActivo(
                form.isActivo()
        );

        return empresaRepository.save(
                empresa
        );
    }

    public void eliminar(Long id) {

        Empresa empresa =
                buscarPorId(id);

        if (insumoRepository.existsByCarpetaEmpresa(
                empresa.getCarpeta())) {

            throw new IllegalArgumentException(
                    "La empresa tiene clases asociadas y no puede eliminarse."
            );
        }

        empresaRepository.delete(
                empresa
        );
    }

    private String obligatorio(
            String valor,
            String mensaje) {

        if (valor == null
                || valor.isBlank()) {

            throw new IllegalArgumentException(
                    mensaje
            );
        }

        return valor.trim();
    }

    private String opcional(String valor) {

        if (valor == null
                || valor.isBlank()) {

            return null;
        }

        return valor.trim();
    }
}