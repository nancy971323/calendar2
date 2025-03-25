@echo off
chcp 65001
echo [INFO] 開始安裝 Maven...
echo [INFO] 設置 PowerShell 執行策略...

powershell -Command "Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser -Force"

echo [INFO] 安裝 Scoop 包管理器...
powershell -Command "Invoke-RestMethod -Uri https://get.scoop.sh | Invoke-Expression"

echo [INFO] 使用 Scoop 安裝 Maven...
powershell -Command "scoop install main/maven"

echo [INFO] Maven 安裝完成！
echo [INFO] 請重新開啟命令提示字元以使用 Maven。

pause 