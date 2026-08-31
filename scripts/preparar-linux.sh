#!/usr/bin/env bash

set -e

echo "============================================"
echo " Bot Engine - Preparacion Linux"
echo "============================================"

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

echo ""
echo "[1/5] Verificando Java..."
if ! command -v java >/dev/null 2>&1; then
    echo "ERROR: Java no esta instalado o no esta en PATH."
    echo "Se requiere Java 17 o superior."
    exit 1
fi
java -version

echo ""
echo "[2/5] Preparando configuracion local..."

if [ ! -f "./config/config.properties" ]; then
    cp "./config/config.example.properties" "./config/config.properties"
    echo "config/config.properties creado."
else
    echo "config/config.properties ya existe."
fi

if [ ! -f "./config/application.properties" ]; then
    cp "./config/application.example.properties" "./config/application.properties"
    echo "config/application.properties creado."
    echo "IMPORTANTE: cambie la clave inicial antes de usar el portal."
else
    echo "config/application.properties ya existe."
fi

echo ""
echo "[3/5] Preparando carpetas..."
mkdir -p ./reportes/testng
mkdir -p ./reportes/evidencias
mkdir -p ./reportes/logs
mkdir -p ./reportes/ejecuciones

echo ""
echo "[4/5] Verificando Maven Wrapper..."
if [ ! -f "./mvnw" ]; then
    echo "ERROR: No se encontro mvnw."
    exit 1
fi

chmod +x ./mvnw
./mvnw -version

echo ""
echo "[5/5] Descargando dependencias y construyendo JAR..."
./mvnw dependency:go-offline
./mvnw clean package -DskipTests

echo ""
echo "============================================"
echo " PREPARACION LINUX COMPLETADA"
echo "============================================"
echo "El servidor ya esta preparado para Bot Engine."