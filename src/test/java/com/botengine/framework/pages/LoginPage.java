package com.botengine.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object base para una pantalla de inicio de sesión.
 * Los localizadores deben ajustarse según la aplicación web evaluada.
 */
public class LoginPage {

    private final WebDriver driver;

    private final By inputUsuario = By.id("username");
    private final By inputPassword = By.id("password");
    private final By botonIngresar = By.id("loginButton");
    private final By mensajeResultado = By.id("message");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void ingresarUsuario(String usuario) {
        driver.findElement(inputUsuario).clear();
        driver.findElement(inputUsuario).sendKeys(usuario);
    }

    public void ingresarPassword(String password) {
        driver.findElement(inputPassword).clear();
        driver.findElement(inputPassword).sendKeys(password);
    }

    public void presionarIngresar() {
        driver.findElement(botonIngresar).click();
    }

    public String obtenerMensajeResultado() {
        return driver.findElement(mensajeResultado).getText();
    }

    public void iniciarSesion(String usuario, String password) {
        ingresarUsuario(usuario);
        ingresarPassword(password);
        presionarIngresar();
    }
}
