package com.botengine.automatizacion.dto;

public class CredencialSitioForm {

    private Long id;
    private Long empresaId;
    private String alias;
    private String url;
    private String username;
    private String password;
    private Integer prioridad = 1;
    private boolean activo = true;

    private boolean usuarioConfigurado;
    private boolean passwordConfigurado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isUsuarioConfigurado() {
        return usuarioConfigurado;
    }

    public void setUsuarioConfigurado(
            boolean usuarioConfigurado) {

        this.usuarioConfigurado =
                usuarioConfigurado;
    }

    public boolean isPasswordConfigurado() {
        return passwordConfigurado;
    }

    public void setPasswordConfigurado(
            boolean passwordConfigurado) {

        this.passwordConfigurado =
                passwordConfigurado;
    }
}