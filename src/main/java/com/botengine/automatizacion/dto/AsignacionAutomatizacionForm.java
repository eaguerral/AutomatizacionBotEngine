package com.botengine.automatizacion.dto;

public class AsignacionAutomatizacionForm {

    private Long usuarioId;
    private Long automatizacionId;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getAutomatizacionId() {
        return automatizacionId;
    }

    public void setAutomatizacionId(
            Long automatizacionId) {

        this.automatizacionId =
                automatizacionId;
    }
}