<template>
  <div id="app">
    <Toast position="top-right" />
    <ConfirmDialog></ConfirmDialog>
    <div class="layout-container">
      <header v-if="isLoggedIn">
        <div class="header-content">
          <div class="header-logo">
            <router-link to="/">企業行事曆系統</router-link>
          </div>
          <div class="header-menu">
            <Button @click="$router.push('/')" label="首頁" icon="pi pi-home" class="p-button-text" />
            <Button @click="$router.push('/calendar')" label="行事曆" icon="pi pi-calendar" class="p-button-text" />
            <Button v-if="isLoggedIn && hasHighestAuth" @click="$router.push('/admin')" label="後臺管理" icon="pi pi-cog" class="p-button-text" />
          </div>
          <div class="header-user" v-if="isLoggedIn">
            <span class="user-info">{{ currentUser.fullName }} ({{ securityLevelText }})</span>
            <Button @click="logout" label="登出" icon="pi pi-sign-out" class="p-button-text" />
          </div>
        </div>
      </header>
      <main class="main-content">
        <router-view />
      </main>
      <footer v-if="isLoggedIn">
        <div class="footer-content">
          <p>&copy; 2025 企業行事曆系統</p>
        </div>
      </footer>
    </div>
  </div>
</template>

<script>
import { computed, onMounted } from 'vue'
import { useAuthStore } from './store/modules/auth'
import { useRouter } from 'vue-router'
import { useToast } from 'primevue/usetoast'

export default {
  name: 'App',
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()
    const toast = useToast()

    // 計算屬性
    const isLoggedIn = computed(() => authStore.isAuthenticated)
    const currentUser = computed(() => authStore.currentUser)
    const securityLevelText = computed(() => {
      if (!currentUser.value || !currentUser.value.securityLevel) return ''
      
      const level = currentUser.value.securityLevel
      switch(level) {
        case 'LEVEL_1': return '最高權限'
        case 'LEVEL_2': return '高級權限'
        case 'LEVEL_3': return '中級權限'
        case 'LEVEL_4': return '基本權限'
        default: return level
      }
    })
    const hasHighestAuth = computed(() => {
      if (!currentUser.value || !currentUser.value.securityLevel) return false
      return currentUser.value.securityLevel === 'LEVEL_1'
    })

    // 方法
    const logout = async () => {
      try {
        await authStore.logout()
        router.push('/login')
        toast.add({
          severity: 'success',
          summary: '登出成功',
          detail: '您已成功登出系統',
          life: 3000
        })
      } catch (error) {
        toast.add({
          severity: 'error',
          summary: '登出失敗',
          detail: '請稍後再試',
          life: 3000
        })
      }
    }

    // 生命週期鉤子
    onMounted(async () => {
      // 如果本地存儲中有token，嘗試自動登入
      const token = localStorage.getItem('token')
      if (token) {
        try {
          await authStore.checkAuth()
        } catch (error) {
          console.error('Token驗證失敗:', error)
          localStorage.removeItem('token')
        }
      }
    })

    return {
      isLoggedIn,
      currentUser,
      securityLevelText,
      hasHighestAuth,
      logout
    }
  }
}
</script>

<style>
html, body {
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  height: 100%;
}

#app {
  height: 100%;
}

.layout-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

header {
  background-color: #ffffff;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  padding: 0.5rem 2rem;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-logo a {
  color: #3B82F6;
  font-size: 1.5rem;
  font-weight: 700;
  text-decoration: none;
}

.header-menu {
  display: flex;
  gap: 1rem;
}

.header-user {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-info {
  font-weight: 600;
}

.main-content {
  flex: 1;
  padding: 2rem;
  background-color: #f8f9fa;
}

footer {
  background-color: #ffffff;
  padding: 1rem 2rem;
  border-top: 1px solid #e9ecef;
}

.footer-content {
  text-align: center;
  color: #6c757d;
}
</style>