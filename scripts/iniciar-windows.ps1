$ErrorActionPreference = "Stop"

Write-Host "============================================"
Write-Host " AutomatizacionBotEngine - Inicio Windows"
Write-Host "============================================"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

Write-Host ""
Write-Host "[1/6] Verificando Java..."
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "ERROR: Java no esta instalado o no esta en PATH."
    Write-Host "Se requiere Java 17 o superior."
    exit 1
}
java -version

Write-Host ""
Write-Host "[2/6] Preparando configuracion..."

if (-not (Test-Path ".\config\config.properties")) {
    Copy-Item ".\config\config.example.properties" ".\config\config.properties"
    Write-Host "config\config.properties creado."
} else {
    Write-Host "config\config.properties ya existe."
}

if (-not (Test-Path ".\config\application.properties")) {
    Copy-Item ".\config\application.example.properties" ".\config\application.properties"
    Write-Host "config\application.properties creado."
    Write-Host "IMPORTANTE: cambie la clave inicial en config\application.properties."
} else {
    Write-Host "config\application.properties ya existe."
}

Write-Host ""
Write-Host "[3/6] Preparando carpetas de reportes..."
New-Item -ItemType Directory -Force ".\reportes\testng" | Out-Null
New-Item -ItemType Directory -Force ".\reportes\evidencias" | Out-Null
New-Item -ItemType Directory -Force ".\reportes\logs" | Out-Null
New-Item -ItemType Directory -Force ".\reportes\ejecuciones" | Out-Null

Write-Host ""
Write-Host "[4/6] Verificando Maven Wrapper..."
if (-not (Test-Path ".\mvnw.cmd")) {
    Write-Host "ERROR: No se encontro mvnw.cmd."
    exit 1
}
.\mvnw.cmd -version

Write-Host ""
Write-Host "[5/6] Compilando..."
.\mvnw.cmd clean compile

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: La compilacion fallo."
    exit $LASTEXITCODE
}

Write-Host ""
Write-Host "[6/6] Iniciando AutomatizacionBotEngine..."
Write-Host "URL: http://localhost:8081"
Write-Host "Para detener el servidor presione Ctrl+C."
Write-Host ""

.\mvnw.cmd spring-boot:run