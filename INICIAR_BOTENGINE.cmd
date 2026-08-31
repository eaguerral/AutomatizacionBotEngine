@echo off
title Automatizacion Bot Engine
cd /d "%~dp0"
echo.
echo ============================================
echo        Automatizacion Bot Engine
echo ============================================
echo.
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\iniciar-windows.ps1"
echo.
pause
