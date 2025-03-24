import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

// PrimeVue 相關導入
import PrimeVue from 'primevue/config'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import Password from 'primevue/password'
import Calendar from 'primevue/calendar'
import Dropdown from 'primevue/dropdown'
import Dialog from 'primevue/dialog'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Textarea from 'primevue/textarea'
import Divider from 'primevue/divider'
import Card from 'primevue/card'
import Panel from 'primevue/panel'
import TabView from 'primevue/tabview'
import TabPanel from 'primevue/tabpanel'
import Menu from 'primevue/menu'
import Message from 'primevue/message'
import Toast from 'primevue/toast'
import ToastService from 'primevue/toastservice'
import ConfirmDialog from 'primevue/confirmdialog'
import ConfirmationService from 'primevue/confirmationservice'
import MultiSelect from 'primevue/multiselect'

// PrimeVue 樣式
import 'primevue/resources/themes/saga-blue/theme.css'
import 'primevue/resources/primevue.min.css'
import 'primeicons/primeicons.css'
import 'primeflex/primeflex.css'

const app = createApp(App)

// 註冊 PrimeVue 元件
app.use(PrimeVue)
app.use(ToastService)
app.use(ConfirmationService)

app.component('Button', Button)
app.component('InputText', InputText)
app.component('Password', Password)
app.component('Calendar', Calendar)
app.component('Dropdown', Dropdown)
app.component('Dialog', Dialog)
app.component('DataTable', DataTable)
app.component('Column', Column)
app.component('Textarea', Textarea)
app.component('Divider', Divider)
app.component('Card', Card)
app.component('Panel', Panel)
app.component('TabView', TabView)
app.component('TabPanel', TabPanel)
app.component('Menu', Menu)
app.component('Message', Message)
app.component('Toast', Toast)
app.component('ConfirmDialog', ConfirmDialog)
app.component('MultiSelect', MultiSelect)

// 全局導航守衛
router.beforeEach(async (to, from, next) => {
  // 檢查路由是否需要認證
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 檢查是否已登入
    if (!store.getters['auth/isAuthenticated']) {
      // 嘗試從localStorage恢復身份驗證狀態
      const token = localStorage.getItem('token')
      if (token) {
        try {
          await store.dispatch('auth/checkAuth')
          next() // 已驗證，繼續導航
          return
        } catch (error) {
          console.error('Token驗證失敗:', error)
          // 驗證失敗，清除token並跳轉到登入頁
          localStorage.removeItem('token')
        }
      }
      // 未登入或token驗證失敗，跳轉到登入頁
      next({
        path: '/login',
        query: { redirect: to.fullPath } // 保存原目標路徑
      })
    } else {
      next() // 已登入，繼續導航
    }
  } else {
    next() // 不需要認證的路由，繼續導航
  }
})

app.use(store)
app.use(router)

app.mount('#app')