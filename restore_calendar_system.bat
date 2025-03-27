@echo off
setlocal enabledelayedexpansion

REM Check parameters
if "%~1"=="" (
  echo No backup file path provided, will use the latest backup file...
  
  REM Set backup file directory with relative path
  set "MY_BACKUP_DIR=database_backups"
  echo Directory variable set to: "!MY_BACKUP_DIR!"
  
  REM Check if backup directory exists
  if not exist "!MY_BACKUP_DIR!" (
    echo Error: Backup directory "!MY_BACKUP_DIR!" not found
    echo Creating backup directory...
    
    echo Attempting to create directory: "database_backups"
    md database_backups 2>nul
    if !errorlevel! neq 0 (
      echo Failed to create backup directory with md command.
      echo Current directory: "%CD%"
      
      REM Try with full path
      set "FULL_PATH=%CD%\database_backups"
      echo Will try with full path: "!FULL_PATH!"
      
      md "!FULL_PATH!" 2>nul
      if !errorlevel! neq 0 (
        echo Still failed to create directory with full path.
        echo Checking if we have write permissions...
        
        REM Try creating a test file
        echo test > test_permissions.txt 2>nul
        if !errorlevel! neq 0 (
          echo Cannot write to current directory. You may not have sufficient permissions.
        ) else (
          del test_permissions.txt
          echo Have write permissions, but still cannot create directory.
        )
        
        pause
        exit /b 1
      ) else (
        echo Directory created successfully with full path.
        set "MY_BACKUP_DIR=!FULL_PATH!"
      )
    ) else (
      echo Backup directory created successfully.
    )
    
    echo Please place backup files in the "!MY_BACKUP_DIR!" directory, then run this script again
    pause
    exit /b 1
  )
  
  REM Find the latest SQL backup file
  set "LATEST_BACKUP="
  echo Searching for SQL files in: "!MY_BACKUP_DIR!"
  for /f "delims=" %%i in ('dir /b /od /a-d "!MY_BACKUP_DIR!\*.sql" 2^>nul') do set "LATEST_BACKUP=%%i"
  
  if not defined LATEST_BACKUP (
    echo Error: No .sql backup files found in "!MY_BACKUP_DIR!" directory
    pause
    exit /b 1
  )
  
  set "BACKUP_FILE=!MY_BACKUP_DIR!\!LATEST_BACKUP!"
  echo Found latest backup file: "!BACKUP_FILE!"
) else (
  set "BACKUP_FILE=%~1"
)

REM Check if file exists
if not exist "!BACKUP_FILE!" (
  echo Error: Backup file "!BACKUP_FILE!" does not exist
  pause
  exit /b 1
)

REM Set database name
set "DB_NAME=calendar_system"
set "DB_USER=root"
set "DB_PASSWORD=123456"

REM Confirm restore operation
echo Warning: This will overwrite the current contents of the "!DB_NAME!" database.
set /p REPLY=Continue? (y/n): 
if /i not "%REPLY%"=="y" (
  echo Operation cancelled
  pause
  exit /b 0
)

REM Perform restore operation
echo Restoring "!DB_NAME!" database from "!BACKUP_FILE!"...
mysql -u !DB_USER! -p!DB_PASSWORD! !DB_NAME! < "!BACKUP_FILE!"

REM Check if successful
if !errorlevel! EQU 0 (
  echo Database restore successful!
) else (
  echo Database restore failed!
)
pause

endlocal 