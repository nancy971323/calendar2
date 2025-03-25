@echo off
REM Check parameters
if "%~1"=="" (
  echo Usage: %0 ^<backup_file_path^>
  exit /b 1
)

set BACKUP_FILE=%~1

REM Check if file exists
if not exist "%BACKUP_FILE%" (
  echo Error: Backup file %BACKUP_FILE% does not exist
  exit /b 1
)

REM Set database name
set DB_NAME=calendar_system
set DB_USER=root
set DB_PASSWORD=123456

REM Confirm restore operation
echo Warning: This will overwrite the current contents of the %DB_NAME% database.
set /p REPLY=Continue? (y/n): 
if /i not "%REPLY%"=="y" (
  echo Operation cancelled
  exit /b 0
)

REM Perform restore operation
echo Restoring %DB_NAME% database from %BACKUP_FILE%...
mysql -u %DB_USER% -p%DB_PASSWORD% %DB_NAME% < "%BACKUP_FILE%"

REM Check if successful
if %ERRORLEVEL% EQU 0 (
  echo Database restore successful!
) else (
  echo Database restore failed!
)
pause 