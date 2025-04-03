<template>
  <div class="event-management-container">
    <div class="event-management-header">
      <h2>行事曆管理</h2>
      <Button label="新增事件" icon="pi pi-plus" @click="openNewEventDialog" />
    </div>
    
    <div class="event-management-filters">
      <div class="filter-item">
        <span class="p-input-icon-left">
          <i class="pi pi-search" />
          <InputText v-model="filters.searchText" placeholder="搜尋事件..." @input="applyFilters" />
        </span>
      </div>
      
      <div class="filter-item">
        <label>年份：</label>
        <Dropdown v-model="filters.year" :options="yearOptions" placeholder="年份" @change="applyFilters" />
      </div>
      
      <div class="filter-item">
        <label>月份：</label>
        <Dropdown v-model="filters.month" :options="monthOptions" optionLabel="name" optionValue="value" placeholder="月份" @change="applyFilters" />
      </div>
      
      <div class="filter-item">
        <label>安全等級：</label>
        <Dropdown v-model="filters.securityLevel" :options="securityLevelOptions" optionLabel="name" optionValue="value" placeholder="安全等級" @change="applyFilters" />
      </div>
      
      <div class="filter-item">
        <ToggleButton v-model="showAllEvents" onLabel="顯示全部事件" offLabel="僅顯示所選年月" @change="toggleShowAllEvents" />
      </div>
      
      <Button icon="pi pi-filter-slash" class="p-button-outlined" @click="resetFilters" />
    </div>
    
    <div class="event-management-content">
      <DataTable
        :value="filteredEvents"
        :paginator="true"
        :rows="10"
        :rowsPerPageOptions="[5, 10, 20, 50]"
        paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
        currentPageReportTemplate="顯示 {first} 到 {last} 共 {totalRecords} 筆記錄"
        responsiveLayout="scroll"
        stripedRows
        class="event-table"
      >
        <Column field="id" header="ID" style="width: 5%"></Column>
        <Column field="title" header="標題" style="width: 15%"></Column>
        <Column field="startTime" header="開始時間" style="width: 15%">
          <template #body="slotProps">
            {{ formatDateTime(slotProps.data.startTime) }}
          </template>
        </Column>
        <Column field="endTime" header="結束時間" style="width: 15%">
          <template #body="slotProps">
            {{ formatDateTime(slotProps.data.endTime) }}
          </template>
        </Column>
        <Column field="location" header="地點" style="width: 10%"></Column>
        <Column field="securityLevel" header="安全等級" style="width: 10%">
          <template #body="slotProps">
            <SecurityLevelBadge :level="slotProps.data.securityLevel" />
          </template>
        </Column>
        <Column field="creatorUsername" header="創建者" style="width: 10%"></Column>
        <Column header="操作" style="width: 15%">
          <template #body="slotProps">
            <Button icon="pi pi-eye" class="p-button-rounded p-button-info p-button-sm" @click="viewEvent(slotProps.data)" />
            <Button icon="pi pi-pencil" class="p-button-rounded p-button-secondary p-button-sm" @click="editEvent(slotProps.data)" />
            <Button icon="pi pi-trash" class="p-button-rounded p-button-danger p-button-sm" @click="confirmDeleteEvent(slotProps.data)" />
          </template>
        </Column>
      </DataTable>
    </div>
    
    <!-- 新增事件對話框 -->
    <Dialog v-model:visible="newEventDialog" header="新增事件" :style="{ width: '500px' }" :modal="true">
      <div class="p-fluid">
        <div class="p-field">
          <label for="event-title">標題</label>
          <InputText id="event-title" v-model.trim="newEvent.title" placeholder="事件標題" />
        </div>
        
        <div class="p-field">
          <label for="event-description">描述</label>
          <Textarea id="event-description" v-model="newEvent.description" rows="3" placeholder="事件描述" />
        </div>
        
        <div class="p-field">
          <label for="event-start">開始時間</label>
          <Calendar id="event-start" v-model="newEvent.startTime" showTime hourFormat="24" placeholder="開始時間" />
        </div>
        
        <div class="p-field">
          <label for="event-end">結束時間</label>
          <Calendar id="event-end" v-model="newEvent.endTime" showTime hourFormat="24" placeholder="結束時間" />
        </div>
        
        <div class="p-field">
          <label for="event-location">地點</label>
          <InputText id="event-location" v-model.trim="newEvent.location" placeholder="事件地點" />
        </div>
        
        <div class="p-field">
          <label for="event-security-level">安全等級</label>
          <Dropdown id="event-security-level" v-model="newEvent.securityLevel" :options="securityLevelOptions" optionLabel="name" optionValue="value" placeholder="選擇安全等級" />
        </div>
        
        <div class="p-field" v-if="newEvent.securityLevel && canGrantSpecialPermissions">
          <label for="event-special-viewers">特殊查看權限</label>
          <MultiSelect
            id="event-special-viewers"
            v-model="newEvent.specialViewerIds"
            :options="lowerLevelEmployees"
            optionLabel="fullName"
            optionValue="id"
            placeholder="可選擇低權限員工"
            display="chip"
          />
        </div>
      </div>
      
      <template #footer>
        <Button label="取消" icon="pi pi-times" class="p-button-text" @click="newEventDialog = false" />
        <Button label="儲存" icon="pi pi-check" @click="saveNewEvent" :loading="savingEvent" />
      </template>
    </Dialog>
    
    <!-- 事件詳情對話框 -->
    <Dialog v-model:visible="eventDetailsDialog" :header="selectedEvent?.title || '事件詳情'" :style="{ width: '500px' }" :modal="true">
      <div v-if="selectedEvent" class="event-details">
        <div class="event-detail-item">
          <span class="detail-label">時間：</span>
          <span class="detail-value">{{ formatEventDateTime(selectedEvent) }}</span>
        </div>
        
        <div class="event-detail-item">
          <span class="detail-label">地點：</span>
          <span class="detail-value">{{ selectedEvent.location || '無地點' }}</span>
        </div>
        
        <div class="event-detail-item">
          <span class="detail-label">安全等級：</span>
          <span class="detail-value">{{ getSecurityLevelName(selectedEvent.securityLevel) }}</span>
        </div>
        
        <div class="event-detail-item">
          <span class="detail-label">創建者：</span>
          <span class="detail-value">{{ selectedEvent.creatorUsername }}</span>
        </div>
        
        <Divider />
        
        <div class="event-description">
          <h3>事件描述</h3>
          <p>{{ selectedEvent.description || '無描述' }}</p>
        </div>
        
        <div v-if="isEventCreator && specialViewersDisplay.length > 0" class="event-special-viewers">
          <h3>特殊權限人員</h3>
          <ul>
            <li v-for="viewer in specialViewersDisplay" :key="viewer.id">
              {{ viewer.fullName }}
            </li>
          </ul>
        </div>
      </div>
      
      <template #footer>
        <div class="event-actions">
          <Button v-if="canEditEvent" label="編輯" icon="pi pi-pencil" class="p-button-secondary" @click="editEventFromDetails" />
          <Button v-if="canDeleteEvent" label="刪除" icon="pi pi-trash" class="p-button-danger" @click="confirmDeleteEventFromDetails" />
          <Button label="關閉" icon="pi pi-times" class="p-button-text" @click="eventDetailsDialog = false" />
        </div>
      </template>
    </Dialog>
    
    <!-- 編輯事件對話框 -->
    <Dialog v-model:visible="editEventDialog" header="編輯事件" :style="{ width: '500px' }" :modal="true">
      <div v-if="editingEvent" class="p-fluid">
        <div class="p-field">
          <label for="edit-event-title">標題</label>
          <InputText id="edit-event-title" v-model.trim="editingEvent.title" placeholder="事件標題" />
        </div>
        
        <div class="p-field">
          <label for="edit-event-description">描述</label>
          <Textarea id="edit-event-description" v-model="editingEvent.description" rows="3" placeholder="事件描述" />
        </div>
        
        <div class="p-field">
          <label for="edit-event-start">開始時間</label>
          <Calendar id="edit-event-start" v-model="editingEvent.startTime" showTime hourFormat="24" placeholder="開始時間" />
        </div>
        
        <div class="p-field">
          <label for="edit-event-end">結束時間</label>
          <Calendar id="edit-event-end" v-model="editingEvent.endTime" showTime hourFormat="24" placeholder="結束時間" />
        </div>
        
        <div class="p-field">
          <label for="edit-event-location">地點</label>
          <InputText id="edit-event-location" v-model.trim="editingEvent.location" placeholder="事件地點" />
        </div>
        
        <div class="p-field">
          <label for="edit-event-security-level">安全等級</label>
          <Dropdown id="edit-event-security-level" v-model="editingEvent.securityLevel" :options="securityLevelOptions" optionLabel="name" optionValue="value" placeholder="選擇安全等級" />
        </div>
        
        <div class="p-field" v-if="editingEvent.securityLevel && canGrantSpecialPermissions">
          <label for="edit-event-special-viewers">特殊查看權限</label>
          <MultiSelect
            id="edit-event-special-viewers"
            v-model="editingEvent.specialViewerIds"
            :options="lowerLevelEmployees"
            optionLabel="fullName"
            optionValue="id"
            placeholder="可選擇低權限員工"
            display="chip"
          />
        </div>
      </div>
      
      <template #footer>
        <Button label="取消" icon="pi pi-times" class="p-button-text" @click="editEventDialog = false" />
        <Button label="儲存" icon="pi pi-check" @click="saveEditedEvent" :loading="savingEvent" />
      </template>
    </Dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import { useCalendarStore } from '@/store/modules/calendar'
import { useEmployeeStore } from '@/store/modules/employee'
import { useAuthStore } from '@/store/modules/auth'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import moment from 'moment'
import SecurityLevelBadge from './shared/SecurityLevelBadge.vue'

export default {
  name: 'EventManagementComponent',
  
  components: {
    SecurityLevelBadge
  },
  
  setup() {
    const calendarStore = useCalendarStore()
    const employeeStore = useEmployeeStore()
    const authStore = useAuthStore()
    const toast = useToast()
    const confirm = useConfirm()
    
    // 數據加載狀態
    const isLoading = ref(false)
    
    // 事件列表
    const events = computed(() => calendarStore.events)
    
    // 篩選器
    const filters = ref({
      month: moment().month() + 1,
      year: moment().year(),
      securityLevel: null,
      searchText: ''
    })
    
    // 是否顯示全部事件
    const showAllEvents = ref(false)
    
    // 根據篩選條件過濾事件
    const filteredEvents = computed(() => {
      let result = [...events.value]
      
      // 搜尋關鍵字過濾
      if (filters.value.searchText) {
        const search = filters.value.searchText.toLowerCase()
        result = result.filter(event => 
          event.title.toLowerCase().includes(search) ||
          (event.description && event.description.toLowerCase().includes(search)) ||
          (event.location && event.location.toLowerCase().includes(search))
        )
      }
      
      // 安全等級過濾
      if (filters.value.securityLevel) {
        result = result.filter(event => event.securityLevel === filters.value.securityLevel)
      }
      
      return result
    })
    
    // 對話框狀態
    const newEventDialog = ref(false)
    const eventDetailsDialog = ref(false)
    const editEventDialog = ref(false)
    
    // 事件相關
    const newEvent = ref({
      title: '',
      description: '',
      startTime: null,
      endTime: null,
      location: '',
      securityLevel: null,
      specialViewerIds: []
    })
    
    const selectedEvent = ref(null)
    const editingEvent = ref(null)
    const savingEvent = ref(false)
    
    // 下拉選項
    const yearOptions = computed(() => {
      const currentYear = new Date().getFullYear()
      return [
        currentYear - 2,
        currentYear - 1,
        currentYear,
        currentYear + 1,
        currentYear + 2
      ]
    })
    
    const monthOptions = ref([
      { name: '一月', value: 1 },
      { name: '二月', value: 2 },
      { name: '三月', value: 3 },
      { name: '四月', value: 4 },
      { name: '五月', value: 5 },
      { name: '六月', value: 6 },
      { name: '七月', value: 7 },
      { name: '八月', value: 8 },
      { name: '九月', value: 9 },
      { name: '十月', value: 10 },
      { name: '十一月', value: 11 },
      { name: '十二月', value: 12 }
    ])
    
    // 安全等級選項
    const securityLevels = computed(() => employeeStore.securityLevels)
    
    const securityLevelOptions = ref([])
    
    // 更新安全等級選項
    const updateSecurityLevelOptions = () => {
      if (!securityLevels.value) return;
      
      let options = [];
      
      try {
        if (Array.isArray(securityLevels.value)) {
          // 如果是數組格式
          options = securityLevels.value.map(level => ({
            name: level.description,
            value: level.code
          }));
        } else {
          // 如果是對象格式 (key-value 形式)
          options = Object.keys(securityLevels.value).map(key => ({
            name: securityLevels.value[key].description,
            value: key
          }));
        }
        
        // 如果沒有當前用戶或沒有安全等級，返回所有選項
        if (!currentUser.value || !currentUser.value.securityLevel) {
          securityLevelOptions.value = options;
          return;
        }
        
        // 根據當前用戶安全等級過濾選項
        // 安全等級數字越小權限越高
        // LEVEL_1: 最高權限, LEVEL_2: 高級權限, LEVEL_3: 中級權限, LEVEL_4: 基本權限
        securityLevelOptions.value = options.filter(option => {
          // 獲取選項等級的數字部分
          const optionLevelNum = parseInt(option.value.split('_')[1])
          // 獲取當前用戶等級的數字部分
          const userLevelNum = parseInt(currentUser.value.securityLevel.split('_')[1])
          // 只返回不高於當前用戶等級的選項（數字不小於用戶等級）
          return optionLevelNum >= userLevelNum
        });
      } catch (error) {
        console.error('處理安全等級選項時出錯:', error);
        securityLevelOptions.value = [];
      }
    }
    
    // 監聽安全等級變化
    watch(securityLevels, () => {
      updateSecurityLevelOptions();
    }, { immediate: true });
    
    // 監聽當前用戶變化
    watch(currentUser, () => {
      updateSecurityLevelOptions();
    }, { immediate: true });
    
    // 計算屬性
    const currentUser = computed(() => authStore.currentUser)
    const employees = computed(() => employeeStore.employees)
    
    // 低權限員工列表（用於特殊權限設定）
    const lowerLevelEmployees = computed(() => {
      if (!currentUser.value || !currentUser.value.securityLevel || !employees.value) return []
      
      // 根據當前用戶權限過濾低權限員工
      const currentLevel = currentUser.value.securityLevel
      return employees.value.filter(emp => {
        const empLevel = emp.securityLevel
        
        // 安全等級數字越小權限越高
        return empLevel > currentLevel && emp.id !== currentUser.value.id
      })
    })
    
    // 特殊查看權限的員工顯示列表
    const specialViewersDisplay = computed(() => {
      if (!selectedEvent.value || !selectedEvent.value.specialViewerIds || !employees.value) return []
      
      return employees.value.filter(emp => 
        selectedEvent.value.specialViewerIds.includes(emp.id)
      )
    })
    
    // 用戶權限相關
    const isEventCreator = computed(() => {
      return selectedEvent.value && currentUser.value && 
             selectedEvent.value.creatorId === currentUser.value.id
    })
    
    const canEditEvent = computed(() => isEventCreator.value)
    
    const canDeleteEvent = computed(() => isEventCreator.value)
    
    const canGrantSpecialPermissions = computed(() => !!currentUser.value)
    
    // 應用篩選
    const applyFilters = () => {
      // 當搜尋關鍵字或安全等級發生變化時，不需要重新載入數據
      if (filters.value.searchText || filters.value.securityLevel) {
        return
      }
      
      loadEvents()
    }
    
    // 切換顯示全部事件或僅顯示所選年月事件
    const toggleShowAllEvents = async () => {
      console.log("切換顯示模式：", showAllEvents.value ? "全部事件" : "僅所選年月")
      await loadEvents()
    }
    
    // 監聽年月變化，重新加載事件
    watch([() => filters.value.year, () => filters.value.month], async () => {
      console.log("年月變化，重新載入事件")
      await loadEvents()
    })
    
    // 加載事件
    const loadEvents = async () => {
      isLoading.value = true
      try {
        // 設置日期為所選年月
        calendarStore.setCurrentDate(new Date(filters.value.year, filters.value.month - 1, 1))
        
        console.log("載入事件，顯示模式：", showAllEvents.value ? "全部事件" : "僅所選年月")
        
        // 根據是否顯示全部事件選擇API調用
        if (showAllEvents.value) {
          // 獲取全部事件
          console.log("正在獲取全部事件...")
          await calendarStore.fetchAllEvents()
        } else {
          // 獲取所選年月的事件
          console.log("正在獲取所選年月事件...")
          await calendarStore.fetchMonthEvents()
        }
      } catch (error) {
        console.error("事件載入錯誤：", error)
        toast.add({
          severity: 'error',
          summary: '錯誤',
          detail: '無法加載事件列表',
          life: 3000
        })
      } finally {
        isLoading.value = false
      }
    }
    
    // 重置篩選條件
    const resetFilters = () => {
      filters.value = {
        month: moment().month() + 1,
        year: moment().year(),
        securityLevel: null,
        searchText: ''
      }
      loadEvents()
    }
    
    // 打開新增事件對話框
    const openNewEventDialog = () => {
      resetNewEvent()
      
      // 設置默認時間為今天
      const now = moment()
      newEvent.value.startTime = now.hour(9).minute(0).second(0).toDate()
      newEvent.value.endTime = now.hour(10).minute(0).second(0).toDate()
      
      newEventDialog.value = true
    }
    
    // 重置新事件表單
    const resetNewEvent = () => {
      newEvent.value = {
        title: '',
        description: '',
        startTime: null,
        endTime: null,
        location: '',
        securityLevel: currentUser.value?.securityLevel || null,
        specialViewerIds: []
      }
    }
    
    // 保存新事件
    const saveNewEvent = async () => {
      try {
        // 表單驗證
        if (!newEvent.value.title) {
          toast.add({
            severity: 'warn',
            summary: '提示',
            detail: '請輸入事件標題',
            life: 3000
          })
          return
        }
        
        if (!newEvent.value.startTime || !newEvent.value.endTime) {
          toast.add({
            severity: 'warn',
            summary: '提示',
            detail: '請選擇開始和結束時間',
            life: 3000
          })
          return
        }
        
        if (moment(newEvent.value.endTime).isBefore(moment(newEvent.value.startTime))) {
          toast.add({
            severity: 'warn',
            summary: '提示',
            detail: '結束時間不能早於開始時間',
            life: 3000
          })
          return
        }
        
        savingEvent.value = true
        
        await calendarStore.createEvent(newEvent.value)
        
        toast.add({
          severity: 'success',
          summary: '成功',
          detail: '事件已創建',
          life: 3000
        })
        
        newEventDialog.value = false
        resetNewEvent()
        
        // 重新加載事件
        await loadEvents()
      } catch (error) {
        toast.add({
          severity: 'error',
          summary: '錯誤',
          detail: error.response?.data || '創建事件失敗',
          life: 3000
        })
      } finally {
        savingEvent.value = false
      }
    }
    
    // 查看事件詳情
    const viewEvent = async (event) => {
      selectedEvent.value = event
      
      // 獲取事件特殊權限
      if (currentUser.value && event.creatorId === currentUser.value.id) {
        try {
          await calendarStore.fetchEventPermissions(event.id)
        } catch (error) {
          console.error('無法獲取事件權限:', error)
        }
      }
      
      eventDetailsDialog.value = true
    }
    
    // 編輯事件
    const editEvent = (event) => {
      // 複製事件對象進行編輯
      editingEvent.value = JSON.parse(JSON.stringify(event))
      
      // 將字串日期轉換為Date物件
      editingEvent.value.startTime = new Date(editingEvent.value.startTime)
      editingEvent.value.endTime = new Date(editingEvent.value.endTime)
      
      editEventDialog.value = true
    }
    
    // 從詳情對話框編輯事件
    const editEventFromDetails = () => {
      eventDetailsDialog.value = false
      editEvent(selectedEvent.value)
    }
    
    // 保存編輯的事件
    const saveEditedEvent = async () => {
      try {
        // 表單驗證
        if (!editingEvent.value.title) {
          toast.add({
            severity: 'warn',
            summary: '提示',
            detail: '請輸入事件標題',
            life: 3000
          })
          return
        }
        
        if (!editingEvent.value.startTime || !editingEvent.value.endTime) {
          toast.add({
            severity: 'warn',
            summary: '提示',
            detail: '請選擇開始和結束時間',
            life: 3000
          })
          return
        }
        
        if (moment(editingEvent.value.endTime).isBefore(moment(editingEvent.value.startTime))) {
          toast.add({
            severity: 'warn',
            summary: '提示',
            detail: '結束時間不能早於開始時間',
            life: 3000
          })
          return
        }
        
        savingEvent.value = true
        
        await calendarStore.updateEventById({
          id: editingEvent.value.id,
          eventData: editingEvent.value
        })
        
        toast.add({
          severity: 'success',
          summary: '成功',
          detail: '事件已更新',
          life: 3000
        })
        
        editEventDialog.value = false
        editingEvent.value = null
        
        // 重新加載事件
        await loadEvents()
      } catch (error) {
        toast.add({
          severity: 'error',
          summary: '錯誤',
          detail: error.response?.data || '更新事件失敗',
          life: 3000
        })
      } finally {
        savingEvent.value = false
      }
    }
    
    // 確認刪除事件
    const confirmDeleteEvent = (event) => {
      confirm.require({
        message: `確定要刪除事件 "${event.title}" 嗎？`,
        header: '刪除確認',
        icon: 'pi pi-exclamation-triangle',
        acceptLabel: '確定',
        rejectLabel: '取消',
        accept: () => deleteEvent(event)
      })
    }
    
    // 從詳情對話框確認刪除事件
    const confirmDeleteEventFromDetails = () => {
      eventDetailsDialog.value = false
      confirmDeleteEvent(selectedEvent.value)
    }
    
    // 刪除事件
    const deleteEvent = async (event) => {
      try {
        await calendarStore.deleteEvent(event.id)
        
        toast.add({
          severity: 'success',
          summary: '成功',
          detail: '事件已刪除',
          life: 3000
        })
        
        // 重新加載事件
        await loadEvents()
      } catch (error) {
        toast.add({
          severity: 'error',
          summary: '錯誤',
          detail: error.response?.data || '刪除事件失敗',
          life: 3000
        })
      }
    }
    
    // 工具方法
    const formatDateTime = (dateTimeStr) => {
      if (!dateTimeStr) return ''
      return moment(dateTimeStr).format('YYYY/MM/DD HH:mm')
    }
    
    const formatEventDateTime = (event) => {
      if (!event) return ''
      
      const start = moment(event.startTime)
      const end = moment(event.endTime)
      
      if (start.isSame(end, 'day')) {
        return `${start.format('YYYY/MM/DD HH:mm')} - ${end.format('HH:mm')}`
      } else {
        return `${start.format('YYYY/MM/DD HH:mm')} - ${end.format('YYYY/MM/DD HH:mm')}`
      }
    }
    
    const getSecurityLevelName = (level) => {
      if (!securityLevels.value || !level) return level
      
      return securityLevels.value[level]?.description || level
    }
    
    // 初始化
    onMounted(async () => {
      // 加載安全等級
      try {
        await employeeStore.fetchSecurityLevels()
      } catch (error) {
        console.error('無法加載安全等級:', error)
      }
      
      // 加載員工列表
      try {
        await employeeStore.fetchActiveEmployees()
      } catch (error) {
        console.error('無法加載員工列表:', error)
      }
      
      // 加載事件
      await loadEvents()
    })
    
    return {
      events,
      filteredEvents,
      filters,
      yearOptions,
      monthOptions,
      securityLevelOptions,
      newEventDialog,
      eventDetailsDialog,
      editEventDialog,
      newEvent,
      selectedEvent,
      editingEvent,
      savingEvent,
      employees,
      lowerLevelEmployees,
      specialViewersDisplay,
      isEventCreator,
      canEditEvent,
      canDeleteEvent,
      canGrantSpecialPermissions,
      resetFilters,
      applyFilters,
      openNewEventDialog,
      saveNewEvent,
      viewEvent,
      editEvent,
      editEventFromDetails,
      saveEditedEvent,
      confirmDeleteEvent,
      confirmDeleteEventFromDetails,
      formatDateTime,
      formatEventDateTime,
      getSecurityLevelName,
      isLoading,
      showAllEvents,
      toggleShowAllEvents
    }
  }
}
</script>

<style scoped>
.event-management-container {
  padding: 1rem 0;
}

.event-management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.event-management-header h2 {
  margin: 0;
  color: #3B82F6;
}

.event-management-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 1.5rem;
  align-items: center;
  background-color: #f8f9fa;
  padding: 1rem;
  border-radius: 8px;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.event-table {
  margin-top: 1rem;
}

:deep(.security-level-badge) {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-weight: bold;
}

:deep(.security-level-1) {
  background-color: #ffcdd2;
  color: #c62828;
}

:deep(.security-level-2) {
  background-color: #fff9c4;
  color: #f57f17;
}

:deep(.security-level-3) {
  background-color: #b3e5fc;
  color: #0277bd;
}

:deep(.security-level-4) {
  background-color: #c8e6c9;
  color: #2e7d32;
}

/* 事件詳情 */
.event-details {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.event-detail-item {
  display: flex;
}

.detail-label {
  font-weight: 600;
  width: 80px;
}

.event-description h3, .event-special-viewers h3 {
  margin-top: 0.5rem;
  margin-bottom: 0.5rem;
  font-size: 1rem;
}

.event-actions {
  display: flex;
  gap: 0.5rem;
}

.p-field {
  margin-bottom: 1.5rem;
}

@media (max-width: 768px) {
  .event-management-filters {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .filter-item {
    width: 100%;
  }
}
</style>