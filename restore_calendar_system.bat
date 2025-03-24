@echo off
REM 檢查參數
if "%~1"=="" (
  echo 用法: %0 ^<備份文件路徑^>
  exit /b 1
)

set BACKUP_FILE=%~1

REM 檢查文件是否存在
if not exist "%BACKUP_FILE%" (
  echo 錯誤: 備份文件 %BACKUP_FILE% 不存在
  exit /b 1
)

REM 設置數據庫名稱
set DB_NAME=calendar_system

REM 確認恢復操作
echo 警告: 這將覆蓋 %DB_NAME% 數據庫的當前內容。
set /p REPLY=是否繼續? (y/n): 
if /i not "%REPLY%"=="y" (
  echo 操作已取消
  exit /b 0
)

REM 執行恢復操作
echo 正在從 %BACKUP_FILE% 恢復 %DB_NAME% 數據庫...
mysql -u root %DB_NAME% < "%BACKUP_FILE%"

REM 檢查是否成功
if %ERRORLEVEL% EQU 0 (
  echo 數據庫恢復成功！
) else (
  echo 數據庫恢復失敗！
) 