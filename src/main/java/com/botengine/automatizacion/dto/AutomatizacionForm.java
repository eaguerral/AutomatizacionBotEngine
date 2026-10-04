package com.botengine.automatizacion.dto;

public class AutomatizacionForm {

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String claseTest;
    private String sitioCodigo;
    private String archivoXml;
    private boolean activo = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}