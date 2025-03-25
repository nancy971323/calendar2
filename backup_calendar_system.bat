@echo off
REM 設置變數
set DB_NAME=calendar_system
set BACKUP_DIR=%USERPROFILE%\Documents\GitHub\calendar2\database_backups
set DATE=%DATE:~0,4%%DATE:~5,2%%DATE:~8,2%_%TIME:~0,2%%TIME:~3,2%%TIME:~6,2%
set DATE=%DATE: =0%
set BACKUP_FILE=%BACKUP_DIR%\calendar_system_backup_%DATE%.sql

REM 確保備份目錄存在
if not exist %BACKUP_DIR% mkdir %BACKUP_DIR%

REM 執行備份
echo 正在備份 %DB_NAME% 數據庫到 %BACKUP_FILE%...
mysqldump -u root --add-drop-table --databases %DB_NAME% --routines --triggers --events > "%BACKUP_FILE%"

REM 檢查備份是否成功
if %ERRORLEVEL% EQU 0 (
  echo 備份成功！檔案位置: %BACKUP_FILE%
  echo 檔案大小:
  for %%F in ("%BACKUP_FILE%") do echo %%~zF 字節
) else (
  echo 備份失敗！
) 