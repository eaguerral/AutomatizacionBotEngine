package com.botengine.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {

    private final WebDriver driver;

    private final By inputUsuario = By.id("username");
    private final By inputPassword = By.id("password");
    private final By botonIngresar = By.cssSelector("button[type='submit']");

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

    public void iniciarSesion(String usuario, String password) {
        ingresarUsuario(usuario);
        ingresarPassword(password);
        presionarIngresar();
    }
}