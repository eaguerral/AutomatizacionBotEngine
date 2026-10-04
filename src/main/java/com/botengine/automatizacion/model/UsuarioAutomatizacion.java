package com.botengine.automatizacion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "usuario_automatizaciones",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_usuario_automatizacion",
                        columnNames = {
                                "usuario_id",
                                "automatizacion_id"
                        }
                )
        }
)
public class UsuarioAutomatizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(nullable = false)
    private boolean activo = true;

    @Column(
            name = "fecha_asignacion",
            nullable = false
    )
    private LocalDateTime fechaAsignacion;

    public UsuarioAutomatizacion() {
    }

    @PrePersist
    public void prePersist() {

        if (fechaAsignacion == null) {
            fechaAsignacion = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }
}