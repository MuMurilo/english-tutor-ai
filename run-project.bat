@echo off
title English Tutor AI - Launcher
echo ====================================================
echo        Iniciando o English Tutor AI...
echo ====================================================
echo.

:: Inicia o Backend em uma nova janela do terminal
echo [1/2] Iniciando o Backend (Quarkus) em uma nova janela...
start "English Tutor - Backend" cmd /c "cd backend && title Backend - Quarkus && color 0A && echo Iniciando Backend... && mvnw.cmd quarkus:dev -Dquarkus.analytics.disabled=true -Dquarkus.enforceBuildGoal=false || pause"

:: Aguarda 5 segundos para o backend começar a subir antes de iniciar o frontend
timeout /t 5 /nobreak > nul

:: Inicia o Frontend em uma nova janela do terminal
echo [2/2] Iniciando o Frontend (Angular) em uma nova janela...
start "English Tutor - Frontend" cmd /c "cd frontend && title Frontend - Angular && color 0B && echo Iniciando Frontend... && npm start || pause"

echo.
echo ====================================================
echo Tudo certo! As janelas do terminal foram abertas.
echo.
echo - O backend estara disponivel em: http://localhost:8080
echo - O frontend estara disponivel em: http://localhost:4200
echo ====================================================
echo Pressione qualquer tecla para fechar este inicializador...
pause > nul
