package com.botengine.automatizacion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "insumos_automatizacion")
public class InsumoAutomatizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            unique = true,
            length = 60
    )
    private String codigo;

    @Column(
            nullable = false,
            length = 120
    )
    private String nombre;

    @Column(
            name = "carpeta_empresa",
            nullable = false,
            length = 80
    )
    private String carpetaEmpresa;

    @Column(length = 300)
    private String descripcion;

    @Column(
            name = "clase_detectada",
            length = 120
    )
    private String claseDetectada;

    @Column(
            name = "archivo_xml_detectado",
            length = 120
    )
    private String archivoXmlDetectado;

    @Column(
            name = "ruta_clase_generada",
            length = 255
    )
    private String rutaClaseGenerada;

    @Column(
            name = "ruta_xml_generada",
            length = 255
    )
    private String rutaXmlGenerada;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private EstadoInsumo estado =
            EstadoInsumo.PENDIENTE;

    @Column(
            name = "fecha_creacion",
            nullable = false
    )
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {

        if (fechaCreacion == null) {
            fechaCreacion =
                    LocalDateTime.now();
        }

        if (estado == null) {
            estado =
                    EstadoInsumo.PENDIENTE;
        }
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCarpetaEmpresa() {
        return carpetaEmpresa;
    }

    public void setCarpetaEmpresa(
            String carpetaEmpresa) {

        this.carpetaEmpresa =
                carpetaEmpresa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(
            String descripcion) {

        this.descripcion =
                descripcion;
    }

    public String getClaseDetectada() {
        return claseDetectada;
    }

    public void setClaseDetectada(
            String claseDetectada) {

        this.claseDetectada =
                claseDetectada;
    }

    public String getArchivoXmlDetectado() {
        return archivoXmlDetectado;
    }

    public void setArchivoXmlDetectado(
            String archivoXmlDetectado) {

        this.archivoXmlDetectado =
                archivoXmlDetectado;
    }

    public String getRutaClaseGenerada() {
        return rutaClaseGenerada;
    }

    public void setRutaClaseGenerada(
            String rutaClaseGenerada) {

        this.rutaClaseGenerada =
                rutaClaseGenerada;
    }

    public String getRutaXmlGenerada() {
        return rutaXmlGenerada;
    }

    public void setRutaXmlGenerada(
            String rutaXmlGenerada) {

        this.rutaXmlGenerada =
                rutaXmlGenerada;
    }

    public EstadoInsumo getEstado() {
        return estado;
    }

    public void setEstado(
            EstadoInsumo estado) {

        this.estado =
                estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}