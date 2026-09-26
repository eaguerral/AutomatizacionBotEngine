#!/usr/bin/env bash

set -e

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

PORT="${PORT:-8081}"

echo "============================================"
echo " Automatizacion Bot Engine - Linux"
echo "============================================"

echo ""
echo "[1/5] Verificando Java..."
if ! command -v java >/dev/null 2>&1; then
    echo "ERROR: Java no esta instalado o no esta en PATH."
    exit 1
fi
java -version

echo ""
echo "[2/5] Verificando configuracion..."

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
echo "[3/5] Preparando carpetas de reportes..."
mkdir -p ./reportes/testng
mkdir -p ./reportes/evidencias
mkdir -p ./reportes/logs
mkdir -p ./reportes/ejecuciones

echo ""
echo "[4/5] Buscando JAR..."
JAR="$(find ./target -maxdepth 1 -type f -name "automatizacion-botengine-*.jar" | head -n 1)"

if [ -z "$JAR" ]; then
    echo "ERROR: No se encontro el JAR de Bot Engine."
    echo "Ejecute primero scripts/preparar-linux.sh"
    exit 1
fi

echo "JAR encontrado: $JAR"

echo ""
echo "[5/5] Iniciando Bot Engine..."
echo "Puerto: $PORT"
echo "Para detener el servidor presione Ctrl+C."
echo ""

java -jar "$JAR" --server.port="$PORT"