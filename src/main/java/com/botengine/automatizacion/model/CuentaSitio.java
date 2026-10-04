package com.botengine.automatizacion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "cuentas_sitio",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cuenta_sitio_alias",
                        columnNames = {
                                "sitio_codigo",
                                "alias"
                        }
                )
        }
)
public class CuentaSitio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "sitio_codigo",
            nullable = false,
            length = 80
    )
    private String sitioCodigo;

    @Column(
            nullable = false,
            length = 120
    )
    private String alias;

    @Column(
            length = 500
    )
    private String url;

    @Column(
            name = "usuario_cifrado",
            length = 2048
    )
    private String usuarioCifrado;

    @Column(
            name = "password_cifrado",
            length = 2048
    )
    private String passwordCifrado;

    @Column(nullable = false)
    private Integer prioridad = 1;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(
            name = "fecha_creacion",
            nullable = false
    )
    private LocalDateTime fechaCreacion;

    public CuentaSitio() {
    }

    @PrePersist
    public void prePersist() {

        if (fechaCreacion == null) {
            fechaCreacion =
                    LocalDateTime.now();
        }

        if (prioridad == null) {
            prioridad = 1;
        }
    }

    public Long getId() {
        return id;
    }

    public String getSitioCodigo() {
        return sitioCodigo;
    }

    public void setSitioCodigo(
            String sitioCodigo) {

        this.sitioCodigo =
                sitioCodigo;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(
            String alias) {

        this.alias =
                alias;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(
            String url) {

        this.url =
                url;
    }

    public String getUsuarioCifrado() {
        return usuarioCifrado;
    }

    public void setUsuarioCifrado(
            String usuarioCifrado) {

        this.usuarioCifrado =
                usuarioCifrado;
    }

    public String getPasswordCifrado() {
        return passwordCifrado;
    }

    public void setPasswordCifrado(
            String passwordCifrado) {

        this.passwordCifrado =
                passwordCifrado;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(
            Integer prioridad) {

        this.prioridad =
                prioridad;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(
            boolean activo) {

        this.activo =
                activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}