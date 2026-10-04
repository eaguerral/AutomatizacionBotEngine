package com.botengine.automatizacion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "automatizaciones")
public class Automatizacion {

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
            length = 300
    )
    private String descripcion;

    @Column(
            name = "clase_test",
            nullable = false,
            unique = true,
            length = 255
    )
    private String claseTest;

    @Column(
            name = "sitio_codigo",
            length = 80
    )
    private String sitioCodigo;

    @Column(
            name = "archivo_xml",
            length = 255
    )
    private String archivoXml;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(
            name = "fecha_creacion",
            nullable = false
    )
    private LocalDateTime fechaCreacion;

    public Automatizacion() {
    }

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getClaseTest() {
        return claseTest;
    }

    public void setClaseTest(String claseTest) {
        this.claseTest = claseTest;
    }

    public String getSitioCodigo() {
        return sitioCodigo;
    }

    public void setSitioCodigo(String sitioCodigo) {
        this.sitioCodigo = sitioCodigo;
    }

    public String getArchivoXml() {
        return archivoXml;
    }

    public void setArchivoXml(String archivoXml) {
        this.archivoXml = archivoXml;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}