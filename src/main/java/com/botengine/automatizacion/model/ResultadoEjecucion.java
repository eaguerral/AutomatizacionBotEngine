package com.botengine.automatizacion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ejecuciones")
public class ResultadoEjecucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            unique = true,
            length = 36
    )
    private String uuid;

    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JoinColumn(
            name = "usuario_id",
            nullable = false
    )
    private Usuario usuario;

    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JoinColumn(
            name = "automatizacion_id",
            nullable = false
    )
    private Automatizacion automatizacion;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private EstadoEjecucion estado;

    @Column(
            name = "fecha_creacion",
            nullable = false
    )
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "duracion_milisegundos")
    private Long duracionMilisegundos;

    @Column(length = 500)
    private String detalle;

    /*
     * Nombre común utilizado por Word, PDF y video.
     * Ejemplo:
     * Reporte_LOGIN_Zentra_MED_a12bc345_20261003_211500
     */
    @Column(
            name = "nombre_base_reporte",
            length = 255
    )
    private String nombreBaseReporte;

    /*
     * Rutas de las evidencias generadas para esta
     * transacción concreta.
     */
    @Column(
            name = "ruta_reporte_word",
            length = 500
    )
    private String rutaReporteWord;

    @Column(
            name = "ruta_reporte_pdf",
            length = 500
    )
    private String rutaReportePdf;

    @Column(
            name = "ruta_video",
            length = 500
    )
    private String rutaVideo;

    @Column(
            name = "ruta_capturas",
            length = 500
    )
    private String rutaCapturas;

    public ResultadoEjecucion() {
    }

    @PrePersist
    public void prePersist() {

        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }

        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }

        if (estado == null) {
            estado = EstadoEjecucion.PENDIENTE;
        }
    }

    public Long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(
            Usuario usuario) {

        this.usuario = usuario;
    }

    public Automatizacion getAutomatizacion() {
        return automatizacion;
    }

    public void setAutomatizacion(
            Automatizacion automatizacion) {

        this.automatizacion =
                automatizacion;
    }

    public EstadoEjecucion getEstado() {
        return estado;
    }

    public void setEstado(
            EstadoEjecucion estado) {

        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(
            LocalDateTime fechaInicio) {

        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(
            LocalDateTime fechaFin) {

        this.fechaFin = fechaFin;
    }

    public Long getDuracionMilisegundos() {
        return duracionMilisegundos;
    }

    public void setDuracionMilisegundos(
            Long duracionMilisegundos) {

        this.duracionMilisegundos =
                duracionMilisegundos;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(
            String detalle) {

        this.detalle = detalle;
    }

    public String getNombreBaseReporte() {
        return nombreBaseReporte;
    }

    public void setNombreBaseReporte(
            String nombreBaseReporte) {

        this.nombreBaseReporte =
                nombreBaseReporte;
    }

    public String getRutaReporteWord() {
        return rutaReporteWord;
    }

    public void setRutaReporteWord(
            String rutaReporteWord) {

        this.rutaReporteWord =
                rutaReporteWord;
    }

    public String getRutaReportePdf() {
        return rutaReportePdf;
    }

    public void setRutaReportePdf(
            String rutaReportePdf) {

        this.rutaReportePdf =
                rutaReportePdf;
    }

    public String getRutaVideo() {
        return rutaVideo;
    }

    public void setRutaVideo(
            String rutaVideo) {

        this.rutaVideo =
                rutaVideo;
    }

    public String getRutaCapturas() {
        return rutaCapturas;
    }

    public void setRutaCapturas(
            String rutaCapturas) {

        this.rutaCapturas =
                rutaCapturas;
    }
}