$ErrorActionPreference = "Stop"

Write-Host "============================================"
Write-Host " Bot Engine - Preparacion Windows"
Write-Host "============================================"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

Write-Host ""
Write-Host "[1/5] Verificando Java..."
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "ERROR: Java no esta instalado o no esta en PATH."
    Write-Host "Se requiere Java 17 o superior."
    exit 1
}
java -version

Write-Host ""
Write-Host "[2/5] Preparando configuracion local..."

if (-not (Test-Path ".\config\config.properties")) {
    Copy-Item ".\config\config.example.properties" ".\config\config.properties"
    Write-Host "config\config.properties creado."
} else {
    Write-Host "config\config.properties ya existe."
}

if (-not (Test-Path ".\config\application.properties")) {
    Copy-Item ".\config\application.example.properties" ".\config\application.properties"
    Write-Host "config\application.properties creado."
    Write-Host "IMPORTANTE: cambie la clave inicial antes de usar el portal."
} else {
    Write-Host "config\application.properties ya existe."
}

Write-Host ""
Write-Host "[3/5] Preparando carpetas..."
New-Item -ItemType Directory -Force ".\reportes\testng" | Out-Null
New-Item -ItemType Directory -Force ".\reportes\evidencias" | Out-Null
New-Item -ItemType Directory -Force ".\reportes\logs" | Out-Null
New-Item -ItemType Directory -Force ".\reportes\ejecuciones" | Out-Null

Write-Host ""
Write-Host "[4/5] Verificando Maven Wrapper..."
if (-not (Test-Path ".\mvnw.cmd")) {
    Write-Host "ERROR: No se encontro mvnw.cmd."
    exit 1
}
.\mvnw.cmd -version

Write-Host ""
Write-Host "[5/5] Descargando dependencias..."
.\mvnw.cmd dependency:go-offline

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: No se pudieron descargar las dependencias."
    exit $LASTEXITCODE
}

.\mvnw.cmd clean compile

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: La compilacion fallo."
    exit $LASTEXITCODE
}

Write-Host ""
Write-Host "============================================"
Write-Host " PREPARACION COMPLETADA"
Write-Host "============================================"
Write-Host "El equipo ya esta preparado para Bot Engine."
Write-Host "Para iniciar use INICIAR_BOTENGINE.cmd."