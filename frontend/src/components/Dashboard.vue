<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>後台管理</h1>
    </div>
    
    <div class="dashboard-content">
      <TabView>
        <TabPanel header="概覽">
          <div class="overview-content">
            <div class="dashboard-cards">
              <Card class="stats-card">
                <template #title>
                  <h2><i class="pi pi-users"></i> 員工統計</h2>
                </template>
                <template #content>
                  <div class="stats-value">{{ stats.employeeCount }}</div>
                  <div class="stats-label">活躍員工</div>
                </template>
              </Card>
              
              <Card class="stats-card">
                <template #title>
                  <h2><i class="pi pi-calendar"></i> 事件統計</h2>
                </template>
                <template #content>
                  <div class="stats-value">{{ stats.eventCount }}</div>
                  <div class="stats-label">本月事件</div>
                </template>
              </Card>
              
              <Card class="stats-card">
                <template #title>
                  <h2><i class="pi pi-shield"></i> 安全等級</h2>
                </template>
                <template #content>
                  <div class="stats-value">{{ stats.securityLevel }}</div>
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
import { useStore } from 'vuex'
import EmployeeManagement from './EmployeeManagement.vue'
import EventManagement from './EventManagement.vue'

export default {
  name: 'DashboardComponent',
  
  components: {
    EmployeeManagement,
    EventManagement
  },
  
  setup() {
    const store = useStore()
    
    // 統計數據
    const stats = ref({
      employeeCount: 0,
      eventCount: 0,
      securityLevel: ''
    })
    
    // 計算屬性
    const currentUser = computed(() => store.getters['auth/currentUser'])
    const employees = computed(() => store.getters['employee/employees'])
    const events = computed(() => store.getters['calendar/events'])
    
    // 格式化安全等級
    const formatSecurityLevel = (level) => {
      if (!level) return ''
      
      switch(level) {
        case 'LEVEL_1': return '最高權限 (等級1)'
        case 'LEVEL_2': return '高級權限 (等級2)'
        case 'LEVEL_3': return '中級權限 (等級3)'
        case 'LEVEL_4': return '基本權限 (等級4)'
        default: return level
      }
    }
    
    // 初始化
    onMounted(async () => {
      // 加載員工列表
      try {
        await store.dispatch('employee/fetchActiveEmployees')
      } catch (error) {
        console.error('無法加載員工列表:', error)
      }
      
      // 加載本月事件
      try {
        await store.dispatch('calendar/fetchMonthEvents')
      } catch (error) {
        console.error('無法加載事件列表:', error)
      }
      
      // 更新統計數據
      stats.value = {
        employeeCount: employees.value.length,
        eventCount: events.value.length,
        securityLevel: formatSecurityLevel(currentUser.value?.securityLevel)
      }
    })
    
    return {
      stats
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