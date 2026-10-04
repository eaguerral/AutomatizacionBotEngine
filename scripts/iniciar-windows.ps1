$ErrorActionPreference = "Stop"

Write-Host "============================================"
Write-Host " AutomatizacionBotEngine - Inicio Windows"
Write-Host "============================================"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root


# ============================================================
# 0. VARIABLES LOCALES
# ============================================================

Write-Host ""
Write-Host "[0/9] Cargando variables locales..."

$envFile = Join-Path $root ".env"

if (-not (Test-Path $envFile)) {

    Write-Host "ERROR: No se encontro el archivo .env."
    Write-Host "Copie .env.example como .env y configure los valores locales."
    exit 1
}

Get-Content $envFile |
ForEach-Object {

    $linea = $_.Trim()

    if (
        -not [string]::IsNullOrWhiteSpace($linea) -and
        -not $linea.StartsWith("#") -and
        $linea.Contains("=")
    ) {

        $partes = $linea.Split("=", 2)

        $nombre = $partes[0].Trim()
        $valor = $partes[1].Trim()

        Set-Item `
            -Path "Env:$nombre" `
            -Value $valor
    }
}

$variablesRequeridas = @(
    "BOTENGINE_DB_NAME",
    "BOTENGINE_DB_USER",
    "BOTENGINE_DB_PASSWORD",
    "BOTENGINE_DB_PORT"
)

foreach ($variable in $variablesRequeridas) {

    $valor =
        [Environment]::GetEnvironmentVariable(
            $variable
        )

    if ([string]::IsNullOrWhiteSpace($valor)) {

        Write-Host "ERROR: Falta la variable $variable en .env."
        exit 1
    }
}

Write-Host "[OK] Variables locales cargadas."
Write-Host "Base de datos: $env:BOTENGINE_DB_NAME"
Write-Host "Puerto BD: $env:BOTENGINE_DB_PORT"
Write-Host "Password BD: CARGADA"


# ============================================================
# 1. DOCKER
# ============================================================

Write-Host ""
Write-Host "[1/9] Verificando Docker..."

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {

    Write-Host "ERROR: Docker no esta instalado o no esta disponible en PATH."
    exit 1
}

docker info *> $null

if ($LASTEXITCODE -ne 0) {

    Write-Host "ERROR: Docker Desktop no esta iniciado."
    Write-Host "Inicie Docker Desktop y vuelva a ejecutar Bot Engine."
    exit 1
}

Write-Host "[OK] Docker disponible."


# ============================================================
# 2. POSTGRESQL
# ============================================================

Write-Host ""
Write-Host "[2/9] Iniciando PostgreSQL..."

if (-not (Test-Path ".\compose-postgres.yml")) {

    Write-Host "ERROR: No se encontro compose-postgres.yml."
    exit 1
}

docker compose `
    --env-file ".env" `
    -f ".\compose-postgres.yml" `
    up -d

if ($LASTEXITCODE -ne 0) {

    Write-Host "ERROR: No fue posible iniciar PostgreSQL."
    exit $LASTEXITCODE
}

Write-Host "Esperando PostgreSQL healthy..."

$postgresHealthy = $false

for ($i = 1; $i -le 30; $i++) {

    $estado =
        docker inspect `
            -f "{{.State.Health.Status}}" `
            automatizacion_botengine_db `
            2>$null

    if ($estado -eq "healthy") {

        $postgresHealthy = $true
        break
    }

    Start-Sleep -Seconds 2
}

if (-not $postgresHealthy) {

    Write-Host "ERROR: PostgreSQL no alcanzo estado healthy."
    docker logs automatizacion_botengine_db --tail 40
    exit 1
}

Write-Host "[OK] PostgreSQL healthy."


# ============================================================
# 3. SELENIUM + VIDEO
# ============================================================

Write-Host ""
Write-Host "[3/9] Iniciando Selenium Chrome y Video..."

if (-not (Test-Path ".\compose-selenium.yml")) {

    Write-Host "ERROR: No se encontro compose-selenium.yml."
    exit 1
}

New-Item `
    -ItemType Directory `
    -Force `
    -Path ".\storage\selenium-videos" |
Out-Null

docker compose `
    -f ".\compose-selenium.yml" `
    up -d

if ($LASTEXITCODE -ne 0) {

    Write-Host "ERROR: No fue posible iniciar Selenium."
    exit $LASTEXITCODE
}

Write-Host "Esperando Selenium Grid..."

$seleniumReady = $false

for ($i = 1; $i -le 40; $i++) {

    try {

        $status =
            Invoke-RestMethod `
                -Uri "http://localhost:4444/status" `
                -Method Get `
                -TimeoutSec 5

        if ($status.value.ready -eq $true) {

            $seleniumReady = $true
            break
        }

    } catch {
    }

    Start-Sleep -Seconds 2
}

if (-not $seleniumReady) {

    Write-Host "ERROR: Selenium Grid no alcanzo estado ready."
    docker logs botengine_selenium_chrome --tail 50
    exit 1
}

$videoRunning =
    docker inspect `
        -f "{{.State.Running}}" `
        botengine_selenium_video `
        2>$null

if ($videoRunning -ne "true") {

    Write-Host "ERROR: El grabador de video Selenium no esta activo."
    docker logs botengine_selenium_video --tail 50
    exit 1
}

Write-Host "[OK] Selenium Grid ready."
Write-Host "[OK] Selenium Video activo."
Write-Host "Grid: http://localhost:4444"
Write-Host "Navegador en vivo: http://localhost:7900"


# ============================================================
# 4. JAVA
# ============================================================

Write-Host ""
Write-Host "[4/9] Verificando Java..."

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {

    Write-Host "ERROR: Java no esta instalado o no esta en PATH."
    Write-Host "Se requiere Java 17 o superior."
    exit 1
}

java -version


# ============================================================
# 5. CONFIGURACION
# ============================================================

Write-Host ""
Write-Host "[5/9] Preparando configuracion..."

if (-not (Test-Path ".\config\config.properties")) {

    Copy-Item `
        ".\config\config.example.properties" `
        ".\config\config.properties"

    Write-Host "config\config.properties creado."

} else {

    Write-Host "config\config.properties ya existe."
}

if (-not (Test-Path ".\config\application.properties")) {

    Copy-Item `
        ".\config\application.example.properties" `
        ".\config\application.properties"

    Write-Host "config\application.properties creado."

} else {

    Write-Host "config\application.properties ya existe."
}


# ============================================================
# 6. CARPETAS
# ============================================================

Write-Host ""
Write-Host "[6/9] Preparando carpetas..."

$carpetas = @(
    ".\reportes\testng",
    ".\reportes\evidencias",
    ".\reportes\logs",
    ".\reportes\ejecuciones",
    ".\storage\evidencias",
    ".\storage\selenium-videos",
    ".\storage\plantillas-evidencia"
)

foreach ($carpeta in $carpetas) {

    New-Item `
        -ItemType Directory `
        -Force `
        -Path $carpeta |
    Out-Null
}

Write-Host "[OK] Carpetas preparadas."


# ============================================================
# 7. MAVEN
# ============================================================

Write-Host ""
Write-Host "[7/9] Verificando Maven Wrapper..."

if (-not (Test-Path ".\mvnw.cmd")) {

    Write-Host "ERROR: No se encontro mvnw.cmd."
    exit 1
}

.\mvnw.cmd -version

if ($LASTEXITCODE -ne 0) {

    Write-Host "ERROR: Maven Wrapper no esta disponible."
    exit $LASTEXITCODE
}


# ============================================================
# 8. COMPILACION
# ============================================================

Write-Host ""
Write-Host "[8/9] Compilando Bot Engine..."

.\mvnw.cmd clean compile

if ($LASTEXITCODE -ne 0) {

    Write-Host "ERROR: La compilacion fallo."
    exit $LASTEXITCODE
}

Write-Host "[OK] BUILD SUCCESS."


# ============================================================
# RESUMEN
# ============================================================

Write-Host ""
Write-Host "============================================"
Write-Host " INFRAESTRUCTURA BOT ENGINE LISTA"
Write-Host "============================================"
Write-Host ""
Write-Host "[OK] PostgreSQL"
Write-Host "[OK] Selenium Chrome"
Write-Host "[OK] Selenium Video"
Write-Host "[OK] Aplicacion compilada"
Write-Host ""
Write-Host "Portal:        http://localhost:8081"
Write-Host "Selenium Grid: http://localhost:4444"
Write-Host "Navegador VNC: http://localhost:7900"
Write-Host ""


# ============================================================
# 9. SPRING BOOT
# ============================================================

Write-Host "[9/9] Iniciando AutomatizacionBotEngine..."
Write-Host "Para detener Spring Boot presione Ctrl+C."
Write-Host ""

.\mvnw.cmd spring-boot:run

exit $LASTEXITCODE