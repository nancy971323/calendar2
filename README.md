# 企業行事曆系統

這是一個多層安全性的企業行事曆系統，支援 Windows 和 Mac 環境。

## 系統需求

- Java 17或更高版本
- Maven 3.8或更高版本
- MySQL 8.0或更高版本
- Node.js 16或更高版本
- npm 8或更高版本

## 安裝指南

### 資料庫設置

1. 安裝 MySQL 資料庫
2. 創建名為 `calendar_system` 的資料庫：
   ```sql
   CREATE DATABASE calendar_system;
   ```

### Windows 環境

1. 使用 `run.bat` 啟動後端應用
2. 使用 `start-frontend.bat` 啟動前端應用
3. 使用 `backup_calendar_system.bat` 備份資料庫
4. 使用 `restore_calendar_system.bat <備份文件>` 還原資料庫

### Mac 環境

1. 為腳本添加執行權限：
   ```bash
   chmod +x *.sh
   ```
2. 使用 `./run.sh` 啟動後端應用
3. 使用 `./start-frontend.sh` 啟動前端應用
4. 使用 `./backup_calendar_system.sh` 備份資料庫
5. 使用 `./restore_calendar_system.sh <備份文件>` 還原資料庫

## 訪問應用

- 前端: http://localhost:8081
- 後端 API: http://localhost:8080

## 開發注意事項

- 使用 Java 的 `Path` 和 `Paths` 類處理文件路徑，以確保跨平台兼容性
- 避免在代碼中使用絕對路徑，使用相對路徑或配置文件中定義的路徑
- 使用 `File.separator` 而不是硬編碼 `/` 或 `\\`
- 注意資源文件的大小寫敏感性，Windows 不區分大小寫，但 Mac/Linux 區分

## 後續計劃

- 支援使用 Docker 部署，進一步提高跨平台兼容性
- 實現自動環境檢測，無需切換配置文件 