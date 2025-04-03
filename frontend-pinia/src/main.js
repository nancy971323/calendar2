// ==================== 核心框架導入 ====================
import { createApp } from 'vue'                   // 導入Vue 3的createApp函數，用於創建Vue應用實例
import App from './App.vue'                       // 導入根組件App.vue
import router from './router'                     // 導入路由配置，通常指向./router/index.js
import pinia from './store'                       // 導入pinia狀態管理配置，通常指向./store/index.js
import { useAuthStore } from './store/modules/auth' // 導入認證相關的狀態管理模組

// ==================== PrimeVue UI庫相關導入 ====================
import PrimeVue from 'primevue/config'            // 導入PrimeVue核心配置
import Button from 'primevue/button'              // 按鈕組件
import InputText from 'primevue/inputtext'        // 文本輸入框組件
import Password from 'primevue/password'          // 密碼輸入框組件
import Calendar from 'primevue/calendar'          // 日曆/日期選擇器組件
import Dropdown from 'primevue/dropdown'          // 下拉選單組件
import Dialog from 'primevue/dialog'              // 對話框/彈窗組件
import DataTable from 'primevue/datatable'        // 數據表格組件
import Column from 'primevue/column'              // 表格列組件
import Textarea from 'primevue/textarea'          // 文本區域組件
import Divider from 'primevue/divider'            // 分隔線組件
import Card from 'primevue/card'                  // 卡片容器組件
import Panel from 'primevue/panel'                // 面板容器組件
import TabView from 'primevue/tabview'            // 選項卡視圖容器
import TabPanel from 'primevue/tabpanel'          // 選項卡面板組件
import Menu from 'primevue/menu'                  // 菜單組件
import Message from 'primevue/message'            // 消息提示組件
import Toast from 'primevue/toast'                // 彈出提示組件
import ToastService from 'primevue/toastservice'  // Toast提示服務
import ConfirmDialog from 'primevue/confirmdialog' // 確認對話框組件
import ConfirmationService from 'primevue/confirmationservice' // 確認服務
import MultiSelect from 'primevue/multiselect'    // 多選下拉選單組件
import ToggleButton from 'primevue/togglebutton'  // 切換按鈕組件

// ==================== PrimeVue 樣式文件導入 ====================
import 'primevue/resources/themes/saga-blue/theme.css' // PrimeVue主題樣式 - saga-blue
import 'primevue/resources/primevue.min.css'           // PrimeVue核心樣式
import 'primeicons/primeicons.css'                     // PrimeIcons圖標庫
import 'primeflex/primeflex.css'                       // PrimeFlex響應式網格系統

// ==================== 創建Vue應用實例 ====================
const app = createApp(App)                        // 使用根組件創建Vue應用實例

// ==================== 註冊PrimeVue插件和服務 ====================
app.use(PrimeVue)                                 // 註冊PrimeVue核心功能
app.use(ToastService)                             // 註冊Toast通知服務
app.use(ConfirmationService)                      // 註冊確認對話框服務

// ==================== 註冊PrimeVue全局組件 ====================
// 這使得這些組件在所有Vue模板中可直接使用，無需局部導入
app.component('Button', Button)                   // 註冊按鈕組件
app.component('InputText', InputText)             // 註冊文本輸入框組件
app.component('Password', Password)               // 註冊密碼輸入框組件
app.component('Calendar', Calendar)               // 註冊日曆組件
app.component('Dropdown', Dropdown)               // 註冊下拉選單組件
app.component('Dialog', Dialog)                   // 註冊對話框組件
app.component('DataTable', DataTable)             // 註冊數據表格組件
app.component('Column', Column)                   // 註冊表格列組件
app.component('Textarea', Textarea)               // 註冊文本區域組件
app.component('Divider', Divider)                 // 註冊分隔線組件
app.component('Card', Card)                       // 註冊卡片組件
app.component('Panel', Panel)                     // 註冊面板組件
app.component('TabView', TabView)                 // 註冊選項卡視圖組件
app.component('TabPanel', TabPanel)               // 註冊選項卡面板組件
app.component('Menu', Menu)                       // 註冊菜單組件
app.component('Message', Message)                 // 註冊消息組件
app.component('Toast', Toast)                     // 註冊Toast提示組件
app.component('ConfirmDialog', ConfirmDialog)     // 註冊確認對話框組件
app.component('MultiSelect', MultiSelect)         // 註冊多選下拉選單組件
app.component('ToggleButton', ToggleButton)       // 註冊切換按鈕組件

// ==================== 全局導航守衛設置 ====================
// 在每次路由跳轉前執行的檢查邏輯
router.beforeEach(async (to, from, next) => {
  // to: 目標路由對象
  // from: 來源路由對象
  // next: 控制路由跳轉的函數

  // 檢查目標路由是否需要認證（通過路由meta屬性設置）
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 獲取認證狀態管理的store實例
    const authStore = useAuthStore()
    
    // 檢查用戶是否已經登入
    if (!authStore.isAuthenticated) {
      // 如果未登入，嘗試從localStorage讀取保存的token
      const token = localStorage.getItem('token')
      if (token) {
        try {
          // 嘗試使用token進行身份驗證
          await authStore.checkAuth()
          
          // 如果需要最高權限，進行額外檢查
          if (to.matched.some(record => record.meta.requiresHighestAuth)) {
            if (!authStore.hasHighestAuth) {
              // 沒有最高權限，重定向到首頁
              next({ path: '/' })
              return // 中止後續代碼執行
            }
          }
          
          // 驗證通過，允許繼續導航
          next()
          return // 中止後續代碼執行
        } catch (error) {
          // token驗證失敗時的錯誤處理
          console.error('Token驗證失敗:', error)
          // 清除無效token
          localStorage.removeItem('token')
        }
      }
      
      // 未登入或token驗證失敗，重定向到登入頁
      next({
        path: '/login',
        query: { redirect: to.fullPath } // 保存原目標路徑，登入後可跳回
      })
    } else {
      // 用戶已登入，檢查是否需要最高權限
      if (to.matched.some(record => record.meta.requiresHighestAuth)) {
        if (!authStore.hasHighestAuth) {
          // 沒有最高權限，重定向到首頁
          next({ path: '/' })
          return // 中止後續代碼執行
        }
      }
      // 已登入且權限足夠，允許導航
      next()
    }
  } else {
    // 路由不需要認證，允許直接導航
    next()
  }
})

// ==================== 註冊核心插件 ====================
app.use(pinia)                                   // 註冊Pinia狀態管理
app.use(router)                                  // 註冊Vue Router路由系統

// ==================== 掛載應用 ====================
app.mount('#app')                                // 將Vue應用掛載到DOM中id為app的元素 