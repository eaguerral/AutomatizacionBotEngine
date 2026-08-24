# Framework base Selenium Java para certificación funcional

Este proyecto contiene una base inicial para documentar el avance funcional del framework de automatización de pruebas funcionales del proyecto de graduación.

## Objetivo

Implementar una estructura base reutilizable para automatizar pruebas funcionales de aplicaciones web mediante Java, Selenium WebDriver, TestNG y Maven.

## Estructura principal

```text
framework-selenium-certificacion/
├── pom.xml
├── testng.xml
├── src/test/java/com/botengine/framework/
│   ├── core/
│   │   ├── BaseTest.java
│   │   └── DriverFactory.java
│   ├── pages/
│   │   └── LoginPage.java
│   ├── tests/
│   │   └── LoginTest.java
│   └── utils/
│       ├── ConfigReader.java
│       ├── ScreenshotListener.java
│       └── ScreenshotUtil.java
└── src/test/resources/
    ├── config/config.properties
    ├── data/datos_prueba_login.csv
    ├── evidencias/
    │   └── matriz_registro_tecnico.csv
    └── reportes/
```

## Requisitos

Java JDK 17 o superior.

Maven instalado.

Google Chrome instalado.

## Configuración inicial

Editar el archivo:

```text
src/test/resources/config/config.properties
```

Cambiar la propiedad `base.url` por la URL de la aplicación web que se usará para la prueba.

También se deben ajustar los localizadores del archivo:

```text
src/test/java/com/botengine/framework/pages/LoginPage.java
```

Los selectores actuales son genéricos y funcionan como plantilla.

## Comando de ejecución

Desde la carpeta raíz del proyecto:

```bash
mvn clean test
```

## Evidencia esperada

Al ejecutar las pruebas se puede obtener evidencia en:

```text
src/test/resources/evidencias/
src/test/resources/reportes/
```

La matriz de registro técnico permite documentar escenario, script, resultado, evidencia e indicador del proceso.

## Nota técnica

Este proyecto es una base inicial. No representa una aplicación web terminada ni un framework completo. Su propósito es evidenciar el inicio de la estructura técnica del framework de automatización funcional.
