<template>
  <div class="home-container">
    <Card class="welcome-card">
      <template #title>
        <h1>歡迎使用企業行事曆系統</h1>
      </template>
      <template #content>
        <div class="welcome-content">
          <p class="welcome-message">
            這是一個為企業設計的行事曆系統，提供多層級的權限控制，讓企業內部資訊能夠適當地分享和保護。
          </p>
          
          <div class="features-section">
            <h2>主要功能</h2>
            <div class="features-grid">
              <div class="feature-item">
                <i class="pi pi-calendar feature-icon"></i>
                <h3>行事曆管理</h3>
                <p>依照月份瀏覽公司事件，清晰明瞭地掌握重要行程。</p>
              </div>
              
              <div class="feature-item">
                <i class="pi pi-shield feature-icon"></i>
                <h3>權限控制</h3>
                <p>四級權限設定，確保敏感事件僅對授權人員可見。</p>
              </div>
              
              <div class="feature-item">
                <i class="pi pi-users feature-icon"></i>
                <h3>員工管理</h3>
                <p>管理員工資料與權限等級，控制資訊存取範圍。</p>
              </div>
              
              <div class="feature-item">
                <i class="pi pi-share-alt feature-icon"></i>
                <h3>特殊權限分享</h3>
                <p>可指定特定低權限員工查看高權限事件，靈活分享資訊。</p>
              </div>
            </div>
          </div>
          
          <div class="action-buttons">
            <Button v-if="isLoggedIn" @click="navigateToCalendar" label="前往行事曆" icon="pi pi-calendar" class="p-button-lg" />
            <Button v-if="isLoggedIn && hasHighestAuth" @click="navigateToAdmin" label="後台管理" icon="pi pi-cog" class="p-button-lg p-button-secondary" />
            <Button v-if="!isLoggedIn" @click="navigateToLogin" label="登入系統" icon="pi pi-sign-in" class="p-button-lg" />
          </div>
        </div>
      </template>
    </Card>
  </div>
</template>

<script>
import { computed, onMounted } from 'vue'
import { useAuthStore } from '@/store/modules/auth'
import { useCalendarStore } from '@/store/modules/calendar'
import { useRouter } from 'vue-router'

export default {
  name: 'HomeComponent',
  
  setup() {
    const authStore = useAuthStore()
    const calendarStore = useCalendarStore()
    const router = useRouter()
    
    const isLoggedIn = computed(() => authStore.isAuthenticated)
    const hasHighestAuth = computed(() => authStore.hasHighestAuth)
    
    // 計算屬性
    const currentUser = computed(() => authStore.currentUser)
    
    // 加載今日事件
    const loadTodayEvents = async () => {
      try {
        calendarStore.setCurrentDate(new Date())
        await calendarStore.fetchMonthEvents()
      } catch (error) {
        console.error('無法加載今日事件:', error)
      }
    }
    
    // 導航方法
    const navigateToCalendar = () => {
      router.push('/calendar')
    }
    
    const navigateToAdmin = () => {
      router.push('/admin')
    }
    
    const navigateToLogin = () => {
      router.push('/login')
    }
    
    onMounted(async () => {
      await loadTodayEvents()
    })
    
    return {
      isLoggedIn,
      hasHighestAuth,
      navigateToCalendar,
      navigateToAdmin,
      navigateToLogin,
      currentUser
    }
  }
}
</script>

<style scoped>
.home-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 200px);
  padding: 2rem;
}

.welcome-card {
  width: 100%;
  max-width: 1000px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.welcome-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.welcome-message {
  font-size: 1.2rem;
  line-height: 1.6;
  color: #495057;
  text-align: center;
}

.features-section {
  margin: 1.5rem 0;
}

.features-section h2 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #3B82F6;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 1.5rem;
  background-color: #f8f9fa;
  border-radius: 8px;
  transition: transform 0.3s ease;
}

.feature-item:hover {
  transform: translateY(-5px);
}

.feature-icon {
  font-size: 2.5rem;
  color: #3B82F6;
  margin-bottom: 1rem;
}

.feature-item h3 {
  font-size: 1.2rem;
  margin-bottom: 0.5rem;
  color: #343a40;
}

.feature-item p {
  color: #6c757d;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 1.5rem;
  margin-top: 1rem;
}

@media (max-width: 768px) {
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .action-buttons button {
    width: 100%;
    margin-bottom: 0.5rem;
  }
}
</style>