@echo off
echo [INFO] Checking for npm installation...

REM Check if npm is installed
npm -v > nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] npm is not installed or not in PATH
    echo [INFO] Installing Node.js and npm...
    
    REM Download Node.js installer (using bitsadmin instead of PowerShell)
    echo [INFO] Downloading Node.js installer...
    bitsadmin /transfer nodeDownload /download /priority normal https://nodejs.org/dist/v18.17.1/node-v18.17.1-x64.msi %CD%\node_installer.msi
    
    REM Install Node.js silently
    echo [INFO] Installing Node.js and npm...
    start /wait msiexec /i node_installer.msi /qn
    
    REM Clean up installer file
    del node_installer.msi
    
    echo [INFO] Node.js and npm installation completed
    
    REM Add npm to PATH
    echo [INFO] Adding npm to PATH...
    setx PATH "%PATH%;C:\Program Files\nodejs" /M
    
    echo [INFO] Please restart command prompt to use npm
) else (
    echo [INFO] npm is already installed
    
    REM Check if npm is in PATH
    echo [INFO] Verifying npm is in PATH...
    where npm > nul 2>&1
    if %ERRORLEVEL% NEQ 0 (
        echo [INFO] npm is installed but not in PATH, adding it...
        for /f "tokens=*" %%i in ('where npm') do set NPM_PATH=%%~dpi
        setx PATH "%PATH%;%NPM_PATH%" /M
        echo [INFO] Added npm directory to PATH
    )
)

echo [INFO] Testing npm access...
call npm -v
if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] npm is now accessible from PATH
) else (
    echo [WARNING] npm might not be accessible until you restart your command prompt
)

pause