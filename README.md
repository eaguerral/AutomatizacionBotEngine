\# AutomatizacionBotEngine



Portal para la gestión y ejecución de pruebas funcionales automatizadas de Bot Engine.



El proyecto integra un portal web desarrollado con Spring Boot y un framework de automatización basado en Selenium WebDriver y TestNG.



============================================================

TECNOLOGÍAS

============================================================



\- Java 17 o superior

\- Spring Boot 3.3.4

\- Spring Security

\- Thymeleaf

\- Selenium WebDriver

\- TestNG

\- Maven Wrapper 3.9.16

\- WebDriverManager



============================================================

RAMAS DEL REPOSITORIO

============================================================



Rama estable:



prod



Rama de desarrollo:



dev



El desarrollo debe realizarse sobre la rama dev.



Para clonar directamente la rama dev:



git clone -b dev --single-branch https://github.com/eaguerral/AutomatizacionBotEngine.git



Después ingresar al proyecto:



cd AutomatizacionBotEngine



============================================================

PREPARACIÓN EN WINDOWS

============================================================



REQUISITO:



La computadora debe tener instalado Java 17 o superior.



Verificar Java:



java -version



No es necesario instalar Maven manualmente.



El proyecto incluye Maven Wrapper y descargará Maven 3.9.16 automáticamente cuando sea necesario.



Después de clonar el repositorio se recomienda ejecutar una vez:



powershell.exe -NoProfile -ExecutionPolicy Bypass -File ".\\scripts\\preparar-windows.ps1"



El script realiza automáticamente:



1\. Verificación de Java.

2\. Creación de archivos de configuración local.

3\. Creación de carpetas para reportes y evidencias.

4\. Verificación de Maven Wrapper.

5\. Descarga de dependencias Maven.

6\. Compilación del proyecto.



Al finalizar debe mostrar:



============================================

&#x20;PREPARACION COMPLETADA

============================================



El equipo ya esta preparado para Bot Engine.



============================================================

CONFIGURACIÓN LOCAL

============================================================



Al preparar el proyecto se generan automáticamente:



config\\config.properties



config\\application.properties



Estos archivos son locales.



NO se almacenan en Git.



Las plantillas disponibles en el repositorio son:



config\\config.example.properties



config\\application.example.properties



\------------------------------------------------------------

CONFIGURACIÓN DEL PORTAL

\------------------------------------------------------------



Archivo:



config\\application.properties



Contenido inicial:



server.port=8081

botengine.admin.username=admin

botengine.admin.password=CAMBIAR\_ESTA\_CLAVE



Antes de trabajar con el portal se debe cambiar:



CAMBIAR\_ESTA\_CLAVE



por una contraseña local.



Ejemplo:



server.port=8081

botengine.admin.username=admin

botengine.admin.password=MiClaveLocal



La contraseña local no se envía al repositorio.



\------------------------------------------------------------

CONFIGURACIÓN DEL FRAMEWORK SELENIUM

\------------------------------------------------------------



Archivo:



config\\config.properties



Este archivo contiene parámetros del framework de automatización.



Ejemplo:



base.url=https://example.com/login

browser=chrome

headless=false

timeout.seconds=15

evidence.path=reportes/evidencias



Estos valores deben adaptarse según la aplicación que se automatizará.



============================================================

INICIAR EL PROYECTO EN WINDOWS

============================================================



La forma recomendada es ejecutar:



INICIAR\_BOTENGINE.cmd



También puede ejecutarse desde PowerShell:



.\\INICIAR\_BOTENGINE.cmd



Otra opción:



powershell.exe -NoProfile -ExecutionPolicy Bypass -File ".\\scripts\\iniciar-windows.ps1"



El script:



1\. Verifica Java.

2\. Verifica las configuraciones.

3\. Crea las carpetas necesarias.

4\. Verifica Maven Wrapper.

5\. Compila el proyecto.

6\. Inicia Spring Boot.



El portal inicia por defecto en:



http://localhost:8081



Para detener el portal:



Ctrl + C



============================================================

PREPARACIÓN EN LINUX

============================================================



El servidor Linux necesita:



\- Git

\- Java 17 o superior

\- Acceso a Internet durante la preparación inicial



Clonar el proyecto:



git clone -b dev --single-branch https://github.com/eaguerral/AutomatizacionBotEngine.git



Ingresar al proyecto:



cd AutomatizacionBotEngine



Preparar el servidor:



./scripts/preparar-linux.sh



El script realiza:



1\. Verificación de Java.

2\. Creación de configuraciones locales.

3\. Creación de carpetas de reportes.

4\. Preparación de Maven Wrapper.

5\. Descarga de dependencias.

6\. Construcción del JAR de Spring Boot.



Los scripts Linux se encuentran versionados con permisos de ejecución.



============================================================

CONFIGURAR CREDENCIALES EN LINUX

============================================================



Después de ejecutar la preparación se debe editar:



config/application.properties



Por ejemplo:



nano config/application.properties



Cambiar:



botengine.admin.password=CAMBIAR\_ESTA\_CLAVE



por una contraseña privada.



============================================================

INICIAR EL PROYECTO EN LINUX

============================================================



Ejecutar:



./scripts/iniciar-linux.sh



El puerto predeterminado es:



8081



Para indicar otro puerto:



PORT=8082 ./scripts/iniciar-linux.sh



Para detener el proceso:



Ctrl + C



============================================================

MAVEN WRAPPER

============================================================



El proyecto incluye Maven Wrapper 3.9.16.



Windows:



.\\mvnw.cmd -version



Linux:



./mvnw -version



Esto permite trabajar con el proyecto sin instalar Maven manualmente.



============================================================

ESTRUCTURA PRINCIPAL DEL PROYECTO

============================================================



AutomatizacionBotEngine

|

|-- .mvn

|   `-- wrapper

|

|-- config

|   |-- application.example.properties

|   `-- config.example.properties

|

|-- reportes

|   |-- evidencias

|   |-- ejecuciones

|   |-- logs

|   `-- testng

|

|-- scripts

|   |-- iniciar-linux.sh

|   |-- iniciar-windows.ps1

|   |-- preparar-linux.sh

|   `-- preparar-windows.ps1

|

|-- src

|   |

|   |-- main

|   |   |

|   |   |-- java

|   |   |   `-- com

|   |   |       `-- botengine

|   |   |           `-- automatizacion

|   |   |

|   |   `-- resources

|   |       |-- banner.txt

|   |       `-- templates

|   |

|   `-- test

|       |

|       |-- java

|       |   `-- com

|       |       `-- botengine

|       |           `-- framework

|       |

|       `-- resources

|

|-- .gitattributes

|-- .gitignore

|-- INICIAR\_BOTENGINE.cmd

|-- mvnw

|-- mvnw.cmd

|-- pom.xml

|-- README.md

`-- testng.xml



============================================================

ESTRUCTURA DEL FRAMEWORK SELENIUM

============================================================



El framework de automatización permanece dentro de:



src/test/java/com/botengine/framework



Actualmente contiene componentes como:



core

pages

tests

utils



Entre las clases existentes se encuentran:



BaseTest.java

DriverFactory.java

LoginPage.java

LoginTest.java

ConfigReader.java

ScreenshotListener.java

ScreenshotUtil.java



============================================================

REPORTES Y EVIDENCIAS

============================================================



Todos los resultados generados durante las ejecuciones deben almacenarse dentro de:



reportes/



La estructura definida es:



reportes/

|

|-- evidencias/

|-- ejecuciones/

|-- logs/

`-- testng/



reportes/evidencias



Contendrá capturas de pantalla y otras evidencias generadas por Selenium.



reportes/testng



Contendrá los reportes generados por TestNG.



reportes/logs



Contendrá archivos relacionados con registros de ejecución.



reportes/ejecuciones



Contendrá información generada durante las ejecuciones.



La carpeta completa:



reportes/



está excluida del repositorio Git.



Esto evita almacenar información relacionada con ejecuciones, evidencias o datos de las aplicaciones probadas.



============================================================

ARCHIVOS QUE NO DEBEN SUBIRSE A GIT

============================================================



Por seguridad y limpieza del repositorio no se versionan:



config/application.properties



config/config.properties



reportes/



target/



.idea/



\*.iml



\*.log



.env



.env.\*



src/test/resources/data/datos\_prueba\_login.csv



pom.xml.backup



pom.xml.paso4.backup



Las plantillas que sí se almacenan son:



config/application.example.properties



config/config.example.properties



src/test/resources/data/datos\_prueba\_login.example.csv



============================================================

SEGURIDAD

============================================================



Las credenciales del portal no se encuentran escritas directamente dentro de SecurityConfig.java.



SecurityConfig obtiene:



botengine.admin.username



y:



botengine.admin.password



desde la configuración local.



Las contraseñas se procesan mediante BCrypt.



El archivo que contiene la contraseña local:



config/application.properties



está excluido de Git.



============================================================

PREPARAR UNA COMPUTADORA NUEVA

============================================================



Flujo recomendado en Windows:



1\. Instalar Java 17 o superior.



2\. Clonar dev:



git clone -b dev --single-branch https://github.com/eaguerral/AutomatizacionBotEngine.git



3\. Entrar al proyecto:



cd AutomatizacionBotEngine



4\. Preparar la computadora:



powershell.exe -NoProfile -ExecutionPolicy Bypass -File ".\\scripts\\preparar-windows.ps1"



5\. Editar:



config\\application.properties



6\. Cambiar la contraseña inicial.



7\. Iniciar:



.\\INICIAR\_BOTENGINE.cmd



8\. Ingresar desde el navegador:



http://localhost:8081



============================================================

PREPARAR UN SERVIDOR LINUX

============================================================



Flujo recomendado:



1\. Instalar Git.



2\. Instalar Java 17 o superior.



3\. Clonar dev:



git clone -b dev --single-branch https://github.com/eaguerral/AutomatizacionBotEngine.git



4\. Entrar al proyecto:



cd AutomatizacionBotEngine



5\. Preparar:



./scripts/preparar-linux.sh



6\. Editar:



config/application.properties



7\. Configurar credenciales.



8\. Iniciar:



./scripts/iniciar-linux.sh



============================================================

ESTADO ACTUAL DEL DESARROLLO

============================================================



Actualmente se encuentra implementado:



\- Framework Selenium Java base.

\- Selenium WebDriver.

\- TestNG.

\- Maven.

\- Maven Wrapper 3.9.16.

\- WebDriverManager.

\- Page Object Model.

\- Portal Spring Boot.

\- Página inicial del portal.

\- Spring Security.

\- Autenticación inicial.

\- Configuración externa de credenciales.

\- BCrypt para contraseñas.

\- Banner Bot Engine.

\- Estructura centralizada de reportes.

\- Exclusión de información sensible mediante .gitignore.

\- Configuración de finales de línea mediante .gitattributes.

\- Scripts automáticos de preparación para Windows.

\- Scripts automáticos de inicio para Windows.

\- Archivo INICIAR\_BOTENGINE.cmd.

\- Scripts automáticos de preparación para Linux.

\- Scripts automáticos de inicio para Linux.

\- Maven Wrapper ejecutable en Linux.

\- Scripts Linux configurados como ejecutables.

\- Rama dev sincronizada con GitHub.

\- Validación mediante clon independiente del repositorio.



============================================================

PRÓXIMAS FASES

============================================================



El desarrollo funcional continuará con:



1\. Gestión de usuarios.

2\. Gestión de empresas.

3\. Relación entre usuarios y empresas.

4\. Roles y permisos.

5\. Dashboard filtrado según empresas asignadas.

6\. Integración del framework Selenium con el portal.

7\. Registro de automatizaciones disponibles.

8\. Ejecución de automatizaciones desde el portal.

9\. Registro del estado de las ejecuciones.

10\. Registro de tiempos.

11\. Capturas de pantalla.

12\. Reportes de resultados.

13\. Historial de ejecuciones.

14\. Métricas e indicadores del proceso.



============================================================

FLUJO DE TRABAJO GIT

============================================================



El desarrollo debe realizarse en:



dev



Verificar rama:



git branch --show-current



Debe mostrar:



dev



Ver estado:



git status -sb



Actualizar desde GitHub:



git pull origin dev



Preparar cambios:



git add -A



Revisar:



git status



Crear commit:



git commit -m "descripcion del cambio"



Enviar:



git push origin dev



La rama prod debe mantenerse como rama estable y no debe recibir cambios directos durante el desarrollo.



============================================================

BOT ENGINE

============================================================



AutomatizacionBotEngine



Portal de gestión y ejecución de pruebas funcionales automatizadas.



Spring Boot + Selenium WebDriver + TestNG + Maven

