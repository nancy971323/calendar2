<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>後台管理</h1>
    </div>
    
    <div class="dashboard-content">
      <TabView v-model:activeIndex="activeTab">
        <TabPanel header="概覽">
          <div class="overview-content">
            <div class="dashboard-cards">
              <Card class="stats-card">
                <template #title>
                  <h2><i class="pi pi-users"></i> 員工統計</h2>
                </template>
                <template #content>
                  <div class="stats-value">{{ employeesCount }}</div>
                  <div class="stats-label">活躍員工</div>
                </template>
              </Card>
              
              <Card class="stats-card">
                <template #title>
                  <h2><i class="pi pi-calendar"></i> 事件統計</h2>
                </template>
                <template #content>
                  <div class="stats-value">{{ eventsCount }}</div>
                  <div class="stats-label">本月事件數量</div>
                </template>
              </Card>
              
              <Card class="stats-card">
                <template #title>
                  <h2><i class="pi pi-shield"></i> 安全等級</h2>
                </template>
                <template #content>
                  <div class="stats-value">{{ getSecurityLevelName(currentUser?.securityLevel) }}</div>
                  <div class="stats-label">您的權限等級</div>
                </template>
              </Card>
            </div>
            
            <div class="dashboard-intro">
              <h2>後台管理說明</h2>
              <p>
                歡迎使用企業行事曆系統後台管理。在此您可以管理員工帳號和行事曆事件，根據您的權限等級，
                您可以執行不同的操作。使用上方的標籤可以切換不同的管理功能。
              </p>
              <ul>
                <li><strong>員工管理：</strong> 創建、編輯和刪除員工帳號，設置員工權限等級。</li>
                <li><strong>行事曆管理：</strong> 管理所有的行事曆事件，包括創建、編輯和刪除事件。</li>
              </ul>
            </div>
          </div>
        </TabPanel>
        
        <TabPanel header="員工管理">
          <EmployeeManagement />
        </TabPanel>
        
        <TabPanel header="行事曆管理">
          <EventManagement />
        </TabPanel>
      </TabView>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/store/modules/auth'
import { useEmployeeStore } from '@/store/modules/employee'
import { useCalendarStore } from '@/store/modules/calendar'
import EmployeeManagement from './EmployeeManagement.vue'
import EventManagement from './EventManagement.vue'

export default {
  name: 'AdminDashboard',
  
  components: {
    EmployeeManagement,
    EventManagement
  },
  
  setup() {
    const authStore = useAuthStore()
    const employeeStore = useEmployeeStore()
    const calendarStore = useCalendarStore()
    
    const activeTab = ref(0)
    const employeesCount = ref(0)
    const eventsCount = ref(0)
    
    const currentUser = computed(() => authStore.currentUser)
    
    // 獲取安全等級名稱映射
    const getSecurityLevelName = (level) => {
      const securityLevelMap = {
        'LEVEL_1': '最高權限管理員',
        'LEVEL_2': '部門主管',
        'LEVEL_3': '一般員工'
      }
      return securityLevelMap[level] || level
    }
    
    // 初始化數據
    const fetchDashboardData = async () => {
      try {
        await employeeStore.fetchEmployees()
        await employeeStore.fetchSecurityLevels()
        await calendarStore.fetchMonthEvents()
        
        employeesCount.value = employeeStore.employees.length
        eventsCount.value = calendarStore.events.length
      } catch (error) {
        console.error('獲取儀表板數據失敗:', error)
      }
    }
    
    onMounted(fetchDashboardData)
    
    return {
      activeTab,
      employeesCount,
      eventsCount,
      currentUser,
      getSecurityLevelName
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  padding: 1.5rem;
}

.dashboard-header {
  margin-bottom: 1.5rem;
}

.dashboard-header h1 {
  margin: 0;
  color: #3B82F6;
  font-size: 1.8rem;
}

.overview-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.dashboard-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}

.stats-card {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.stats-card h2 {
  margin: 0;
  font-size: 1.2rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.stats-card h2 i {
  color: #3B82F6;
}

.stats-value {
  font-size: 2.5rem;
  font-weight: 700;
  color: #3B82F6;
  text-align: center;
  margin: 1rem 0;
}

.stats-label {
  text-align: center;
  color: #6c757d;
  font-size: 1rem;
}

.dashboard-intro {
  background-color: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  border-left: 4px solid #3B82F6;
}

.dashboard-intro h2 {
  margin-top: 0;
  color: #3B82F6;
  font-size: 1.4rem;
}

.dashboard-intro p {
  line-height: 1.6;
  color: #495057;
}

.dashboard-intro ul {
  margin-top: 1rem;
  padding-left: 1.5rem;
}

.dashboard-intro li {
  margin-bottom: 0.5rem;
  color: #495057;
}

@media (max-width: 768px) {
  .dashboard-cards {
    grid-template-columns: 1fr;
  }
}
</style>