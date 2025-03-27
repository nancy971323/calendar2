# Calendar System Frontend (Pinia版本)

這是一個使用Vue 3和Pinia構建的日曆系統前端應用程序。

## 技術棧

- Vue 3
- Vue Router
- Pinia (狀態管理)
- PrimeVue (UI組件庫)
- Axios (HTTP客戶端)
- Moment.js (日期處理)
- Vuelidate (表單驗證)

## 項目設置

```
npm install
```

### 開發模式下編譯和熱重載

```
npm run serve
```

### 生產環境編譯和打包

```
npm run build
```

### 代碼檢查和修復

```
npm run lint
```

## 項目結構

- `src/assets`: 靜態資源文件
- `src/components`: 可復用的Vue組件
- `src/router`: 路由配置
- `src/store`: Pinia狀態管理
  - `modules/auth.js`: 認證相關狀態
  - `modules/calendar.js`: 日曆事件相關狀態
  - `modules/employee.js`: 員工相關狀態
- `src/utils`: 工具函數
- `src/views`: 頁面級組件

## 功能特性

- 用戶認證 (登入/登出)
- 基於角色的權限控制
- 事件管理 (創建、查看、編輯、刪除)
- 員工管理
- 日曆視圖
