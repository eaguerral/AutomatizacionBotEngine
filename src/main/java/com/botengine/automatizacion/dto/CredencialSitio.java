package com.botengine.automatizacion.dto;

public record CredencialSitio(
        String sitioCodigo,
        String alias,
        String url,
        String username,
        String password
) {
}