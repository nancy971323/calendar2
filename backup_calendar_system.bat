@echo off
REM Setup variables
set DB_NAME=calendar_system
set DB_USER=root
set DB_PASSWORD=123456
set BACKUP_DIR=%USERPROFILE%\Documents\GitHub\calendar2\database_backups
set DATE=%DATE:~0,4%%DATE:~5,2%%DATE:~8,2%_%TIME:~0,2%%TIME:~3,2%%TIME:~6,2%
set DATE=%DATE: =0%
set BACKUP_FILE=%BACKUP_DIR%\calendar_system_backup_%DATE%.sql

REM Ensure backup directory exists
if not exist %BACKUP_DIR% mkdir %BACKUP_DIR%

REM Perform backup
echo Backing up %DB_NAME% database to %BACKUP_FILE%...
mysqldump -u %DB_USER% -p%DB_PASSWORD% --add-drop-table --routines --triggers --events %DB_NAME% > "%BACKUP_FILE%"

REM Check if backup was successful
if %ERRORLEVEL% EQU 0 (
  echo Backup successful! File location: %BACKUP_FILE%
  echo File size:
  for %%F in ("%BACKUP_FILE%") do echo %%~zF bytes
) else (
  echo Backup failed!
)
pause 