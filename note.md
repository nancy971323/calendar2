# Vue.js 與 Pinia 筆記

## 需要自己寫的代碼
你需要自己寫的主要是：
- 自定義的組件 (src/components)
- 視圖頁面 (src/views)
- Pinia store 模塊的具體實現 (src/store/modules)
- 自定義的路由配置 (src/router/index.js)
- 工具函數 (src/utils)

特別是在 Pinia 方面，你需要定義 store 的內容（如 state、actions、getters），但 Pinia 本身的狀態管理機制是由框架提供的。

## frontend-pinia 的運作流程（以檔案為單位）

1. **public/index.html**
   - 網站的HTML模板，包含根元素 `#app`，Vue應用會掛載在這裡
   - 提供基本的HTML結構和初始加載頁面

2. **src/main.js**
   - 應用程式的入口點
   - 導入 Vue、Pinia、Vue Router 等核心庫和配置
   - 註冊全局組件（如PrimeVue組件）
   - 設置路由守衛（處理登入驗證邏輯）
   - 創建Vue應用並掛載到 `#app` 元素

3. **src/App.vue**
   - 應用程式的根組件
   - 提供應用的整體布局結構
   - 包含 `<router-view>` 來顯示當前路由對應的視圖

4. **src/router/index.js**
   - 定義應用的路由配置
   - 將URL路徑映射到對應的視圖組件
   - 設置路由的元信息（如是否需要身份驗證）

5. **src/store/index.js**
   - 建立和導出Pinia實例
   - 連接所有的store模塊

6. **src/store/modules/*.js** (auth.js, employee.js, calendar.js)
   - 定義不同領域的數據存儲和操作
   - 使用 Pinia 的 `defineStore` 創建數據倉庫
   - 包含 state（狀態）、getters（計算屬性）和 actions（異步操作）

7. **src/views/**
   - 包含對應路由的頁面組件
   - 組成應用的主要頁面內容
   - 通過 useStore 鉤子使用Pinia的狀態

8. **src/components/**
   - 可重用的UI組件
   - 被頁面組件調用和組合

9. **src/utils/**
   - 工具函數和通用邏輯
   - 提供API調用、格式化等功能

**數據流程**：
- 用戶訪問應用 → index.html → main.js初始化應用 → App.vue渲染基礎框架
- 路由變化 → router決定顯示哪個view → 頁面組件加載
- 頁面需要數據 → 通過Pinia store獲取/修改數據 → 當store中的數據更新時，所有使用該數據的組件自動重新渲染

**Pinia特有流程**：
- 組件通過 `useXxxStore()` 連接到指定的store
- 讀取數據：直接訪問store的state或getters
- 修改數據：調用store中定義的actions或直接修改state
- 數據變化自動反映到所有使用該store的組件中

## Pinia store的運作原理

### 基本架構
1. **數據存儲位置**：
   - Pinia 的數據存儲在**內存**中，作為 JavaScript 對象存在
   - 所有 store 實例由 `createPinia()` 管理（在`src/store/index.js`中創建）
   - 數據僅在當前頁面會話期間存在，重新整理頁面後會重置（除非做了持久化處理）

### 數據流程
1. **定義 Store**：
   ```js
   // src/store/modules/auth.js
   import { defineStore } from 'pinia'
   
   export const useAuthStore = defineStore('auth', {
     state: () => ({
       user: null,
       token: null
     }),
     getters: { ... },
     actions: { ... }
   })
   ```

2. **注册 Store**：
   - 在 `main.js` 中通過 `app.use(pinia)` 將 Pinia 實例注册到 Vue 應用

3. **組件使用 Store**：
   ```js
   import { useAuthStore } from '@/store/modules/auth'
   
   export default {
     setup() {
       const authStore = useAuthStore()
       // 使用 authStore 的數據和方法
       return { authStore }
     }
   }
   ```

### 核心特性
1. **響應式**：
   - Pinia 使用 Vue 3 的響應式系統（`reactive()`、`ref()`）
   - 當 store 中的數據變化時，所有使用該數據的組件會自動更新

2. **模塊化**：
   - 每個功能模塊有自己的 store（auth.js、employee.js、calendar.js）
   - store 之間可以互相引用和使用

3. **持久化**：
   - 默認情況下，數據不會持久化保存
   - 在項目中，從 `main.js` 的路由守衛可以看出，身份驗證信息會存儲到 `localStorage` 中
   ```js
   const token = localStorage.getItem('token')
   ```

4. **數據修改**：
   - 直接修改 state（與 Vuex 不同，Pinia 允許直接修改）
   ```js
   authStore.user = newUser
   ```
   - 通過 actions 修改（推薦用於複雜邏輯或異步操作）
   ```js
   authStore.login(credentials)
   ```

## 前端 store 與後端的職責分工

### 為什麼前端需要 store

1. **即時響應與使用者體驗**
   - 避免頻繁向後端請求資料，減少等待時間
   - 使用者操作後立即看到介面變化，不用等待網路請求

2. **暫存資料管理**
   - 暫存使用者輸入的表單資料
   - 存儲使用者的頁面操作狀態（如選擇了哪個分頁、篩選條件）
   - 保留未提交的變更

3. **跨元件共享資料**
   - 讓不同頁面或元件可以存取共同的資料（如用戶資訊）
   - 避免多個元件各自發送重複的API請求

4. **離線功能支援**
   - 暫存資料使應用在網路不穩定時仍可部分使用
   - 可實現離線優先應用，稍後同步到後端

5. **狀態同步與一致性**
   - 確保整個應用對同一資料有統一的視圖
   - 避免同一資料在不同元件中有不一致的狀態

### 後端的職責

1. **資料永久儲存**
   - 資料庫保存所有持久化資料
   - 確保資料不會因瀏覽器關閉而丟失

2. **業務邏輯與資料驗證**
   - 執行複雜的業務規則
   - 確保資料安全與完整性

3. **授權與認證**
   - 管理使用者權限
   - 保護敏感資料

4. **多使用者資料協調**
   - 處理多人同時操作的資料衝突
   - 確保資料一致性

### 前後端協作模式

1. **資料流向**：
   - 初始資料：後端 → 前端 store → 元件
   - 資料變更：元件 → 前端 store → 後端 → 資料庫
   - 多用戶更新：後端 → 前端 store（通過WebSocket或輪詢）

2. **典型應用場景**：
   - 日曆應用：後端存儲所有活動，前端 store 管理當前視圖、篩選條件、暫存的新活動
   - 表單處理：使用者填寫時資料存在前端 store，提交成功後才發送到後端

## 前端權限管理的安全考量

### 前端權限存儲的風險

1. **易被篡改**
   - 前端的任何數據都可被使用者通過瀏覽器開發工具修改
   - 攻擊者可以直接修改 store 中的權限狀態，繞過前端的權限檢查

2. **無法信任**
   - 前端權限狀態只能作為使用者體驗的優化，不能作為安全邊界
   - JavaScript 代碼運行在使用者的瀏覽器中，完全可被檢視和操作

### 正確的權限管理架構

1. **雙重驗證模式**
   - 前端：存儲權限用於 UI 控制和路由守衛（提升體驗）
   - 後端：**所有請求必須再次驗證權限**（真正的安全邊界）

2. **從代碼看實際實現**
   ```js
   // main.js 中的路由守衛
   if (to.matched.some(record => record.meta.requiresHighestAuth)) {
     if (!authStore.hasHighestAuth) {
       // 僅前端防護 - 阻止訪問高權限頁面
       next({ path: '/' })
       return
     }
   }
   ```

3. **JWT 令牌的作用**
   - 權限可能包含在存儲的 token 中
   - Token 應包含權限信息，但仍需後端驗證每次請求

### 安全實踐

1. **前端權限的正確用途**
   - 控制 UI 元素顯示/隱藏（提升用戶體驗）
   - 防止普通用戶意外訪問無權限頁面
   - 減少不必要的 API 請求

2. **後端必要措施**
   - 每個 API 端點都必須驗證請求的權限
   - 不能信任前端發送的權限聲明
   - 使用 session/token 在後端維護權限狀態

3. **資料存取層級**
   - 敏感資料不應完整發送到前端
   - 根據用戶權限篩選 API 返回的資料內容 