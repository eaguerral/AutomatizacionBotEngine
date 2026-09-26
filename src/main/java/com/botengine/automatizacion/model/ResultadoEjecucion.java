package com.botengine.automatizacion.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ResultadoEjecucion {

    private final String id;
    private final String automatizacion;
    private final String estado;
    private final LocalDateTime fechaInicio;
    private final LocalDateTime fechaFin;
    private final long duracionMilisegundos;

    public ResultadoEjecucion(
            String automatizacion,
            String estado,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            long duracionMilisegundos) {

        this.id = UUID.randomUUID().toString();
        this.automatizacion = automatizacion;
        this.estado = estado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.duracionMilisegundos = duracionMilisegundos;
    }

    public String getId() {
        return id;
    }

    public String getAutomatizacion() {
        return automatizacion;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public long getDuracionMilisegundos() {
        return duracionMilisegundos;
    }
}