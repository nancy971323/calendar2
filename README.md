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

### 系統組件安裝腳本

本系統提供了自動安裝必要組件的腳本：

#### Windows 環境

- `install_nodejs.bat` - 自動安裝 Node.js 和 npm
- `install-maven.bat` - 自動安裝 Maven

#### Mac 環境

- `install_nodejs.sh` - 自動安裝 Node.js 和 npm（支援多種包管理器）

### 後端與前端設置

#### Windows 環境

1. 使用 `run.bat` 啟動後端應用
2. 使用 `start-frontend.bat` 啟動前端應用
3. 使用 `backup_calendar_system.bat` 備份資料庫
4. 使用 `restore_calendar_system.bat <備份文件>` 還原資料庫

#### Mac 環境

1. 為腳本添加執行權限：
   ```bash
   chmod +x *.sh
   ```
2. 使用 `./run.sh` 啟動後端應用
3. 使用 `./start-frontend.sh` 啟動前端應用
4. 使用 `./backup_calendar_system.sh` 備份資料庫
5. 使用 `./restore_calendar_system.sh <備份文件>` 還原資料庫

## 腳本詳細說明

### 啟動腳本

#### Windows 環境

- **run.bat**：啟動後端應用
  ```batch
  cd backend
  mvn spring-boot:run "-Dspring-boot.run.profiles=windows"
  ```

- **start-frontend.bat**：啟動前端應用
  ```batch
  cd frontend
  call npm install
  call npm run serve
  ```

#### Mac 環境

- **run.sh**：啟動後端應用
  ```bash
  cd backend
  mvn spring-boot:run -Dspring.profiles.active=mac
  ```

- **start-frontend.sh**：啟動前端應用
  ```bash
  cd frontend
  npm install
  npm run serve
  ```

### 資料庫備份與還原腳本

#### Windows 環境

- **backup_calendar_system.bat**：備份數據庫
  - 功能：將 calendar_system 數據庫備份至 %USERPROFILE%\Documents\GitHub\calendar2\database_backups 目錄
  - 備份檔案格式：calendar_system_backup_日期_時間.sql
  - 使用方式：直接執行即可

- **restore_calendar_system.bat**：還原數據庫
  - 功能：從指定的 SQL 檔案還原數據庫
  - 參數：<備份檔案路徑>
  - 使用方式：
    ```batch
    restore_calendar_system.bat C:\path\to\backup.sql
    ```
  - 執行前會詢問確認

#### Mac 環境

- **backup_calendar_system.sh**：備份數據庫
  - 功能：將 calendar_system 數據庫備份至 /Users/lidiannan/Documents/GitHub/calendar2/database_backups 目錄
  - 備份檔案格式：calendar_system_backup_日期_時間.sql
  - 使用方式：
    ```bash
    ./backup_calendar_system.sh
    ```

- **restore_calendar_system.sh**：還原數據庫
  - 功能：從指定的 SQL 檔案還原數據庫
  - 參數：<備份檔案路徑>
  - 使用方式：
    ```bash
    ./restore_calendar_system.sh /path/to/backup.sql
    ```
  - 執行前會詢問確認

### 工具安裝腳本

#### Windows 環境

- **install_nodejs.bat**：安裝 Node.js 和 npm
  - 功能：自動下載並安裝 Node.js v18.17.1
  - 特點：
    - 自動檢測 npm 是否已安裝
    - 自動添加 npm 到系統 PATH
    - 安裝完成後顯示版本信息
  - 使用方式：直接執行

- **install-maven.bat**：安裝 Maven
  - 功能：使用 Scoop 包管理器安裝 Maven
  - 特點：
    - 自動設置 PowerShell 執行策略
    - 自動安裝 Scoop 包管理器
    - 使用 Scoop 安裝 Maven
  - 使用方式：直接執行

#### Mac 環境

- **install_nodejs.sh**：安裝 Node.js 和 npm
  - 功能：根據系統自動選擇合適的安裝方式
  - 特點：
    - 支援多種 Linux 發行版和 macOS
    - 自動檢測並使用適合的包管理器（apt、dnf、Homebrew）
    - 安裝 Node.js v18
    - 安裝完成後顯示版本信息
  - 使用方式：
    ```bash
    ./install_nodejs.sh
    ```

## 訪問應用

- 前端: http://localhost:8081
- 後端 API: http://localhost:8080

## 專案管理與清理

### Git版本控制

本專案使用Git進行版本控制，已配置.gitignore以排除不需要版本控制的檔案：

- 執行環境檔案 (node_modules/, target/)
- 環境變數檔案 (.env, .env.*)
- IDE設定檔案 (.idea/, .vscode/, .metadata/)
- 構建輸出 (dist/, build/)
- 日誌檔案 (*.log)
- 臨時檔案和系統檔案 (.DS_Store, *.tmp)

### 清理專案

清理不需要的檔案可使用以下命令：

```bash
# 清理前端依賴
rm -rf frontend/node_modules/

# 清理後端構建檔案
rm -rf backend/target/

# 清理環境變數和臨時檔案
rm -rf .env* .DS_Store tmp/ *.log
```

## 開發注意事項

- 使用 Java 的 `Path` 和 `Paths` 類處理文件路徑，以確保跨平台兼容性
- 避免在代碼中使用絕對路徑，使用相對路徑或配置文件中定義的路徑
- 使用 `File.separator` 而不是硬編碼 `/` 或 `\\`
- 注意資源文件的大小寫敏感性，Windows 不區分大小寫，但 Mac/Linux 區分

## 開發工作流程

1. 克隆專案到本地：
   ```bash
   git clone <專案儲存庫URL>
   ```

2. 安裝依賴：
   ```bash
   # 後端
   cd backend
   mvn install
   
   # 前端
   cd frontend
   npm install
   ```

3. 開發與測試
4. 提交變更：
   ```bash
   git add .
   git commit -m "變更描述"
   git push
   ```

## 後續計劃

- 支援使用 Docker 部署，進一步提高跨平台兼容性
- 實現自動環境檢測，無需切換配置文件
- 改進用戶界面，提升使用體驗
- 增加行事曆共享功能
- 支援多種語言 