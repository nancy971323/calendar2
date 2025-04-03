<template>
  <div class="calendar-container">
    <div class="calendar-header">
      <h1>行事曆 - {{ currentMonthYear }}</h1>
      
      <div class="calendar-controls">
        <Button icon="pi pi-chevron-left" @click="previousMonth" class="p-button-rounded p-button-outlined" />
        <Button @click="goToToday" label="今天" class="p-button-outlined" />
        <Button icon="pi pi-chevron-right" @click="nextMonth" class="p-button-rounded p-button-outlined" />
        
        <Dropdown v-model="selectedMonth" :options="months" optionLabel="name" optionValue="value" class="month-dropdown" />
        <Dropdown v-model="selectedYear" :options="years" class="year-dropdown" />
      </div>
      
      <div class="calendar-actions">
        <Button label="新增事件" icon="pi pi-plus" @click="openNewEventDialog" />
      </div>
    </div>
    
    <div class="calendar-content">
      <div class="calendar-weekdays">
        <div class="weekday" v-for="day in weekdays" :key="day">{{ day }}</div>
      </div>
      
      <div class="calendar-days">
        <div 
          v-for="day in calendarDays" 
          :key="day.date" 
          class="calendar-day" 
          :class="{ 
            'other-month': day.isOtherMonth, 
            'today': day.isToday,
            'has-events': day.events && day.events.length > 0
          }"
          @click="selectDay(day)"
        >
          <div class="day-number">{{ day.dayNumber }}</div>
          
          <div class="day-events">
            <div 
              v-for="event in day.events" 
              :key="event.id" 
              class="day-event" 
              :class="getEventClass(event)"
              @click.stop="openEventDetailsDialog(event)"
            >
              <span class="event-time">{{ formatEventTime(event) }}</span>
              <span class="event-title">{{ event.title }}</span>
            </div>
          </div>
        </div>
      </div>
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
          <Button v-if="canEditEvent" label="編輯" icon="pi pi-pencil" class="p-button-secondary" @click="editEvent" />
          <Button v-if="canDeleteEvent" label="刪除" icon="pi pi-trash" class="p-button-danger" @click="confirmDeleteEvent" />
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
import { ref, computed, watch, onMounted } from 'vue'
import { useCalendarStore } from '@/store/modules/calendar'
import { useEmployeeStore } from '@/store/modules/employee'
import { useAuthStore } from '@/store/modules/auth'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import moment from 'moment'

export default {
  name: 'CalendarComponent',
  
  setup() {
    const calendarStore = useCalendarStore()
    const employeeStore = useEmployeeStore()
    const authStore = useAuthStore()
    const toast = useToast()
    const confirm = useConfirm()
    
    // 日期和數據狀態
    const isLoading = ref(false)
    const selectedDate = ref(moment().format('YYYY-MM-DD'))
    const selectedMonth = ref(moment().month() + 1)
    const selectedYear = ref(moment().year())
    
    // 事件表單狀態
    const isNewEvent = ref(true)
    const newEventDialog = ref(false)
    const eventDetailsDialog = ref(false)
    const editEventDialog = ref(false)
    
    // 事件相關
    const selectedEvent = ref(null)
    const editingEvent = ref(null)
    const savingEvent = ref(false)
    
    const newEvent = ref({
      title: '',
      description: '',
      startTime: null,
      endTime: null,
      securityLevel: 'LEVEL_3',
      location: '',
      isAllDay: false
    })
    
    // 計算屬性
    const currentUser = computed(() => authStore.currentUser)
    const securityLevels = computed(() => employeeStore.securityLevels)
    const employees = computed(() => employeeStore.employees)
    
    // 日曆上顯示的日期
    const calendarDays = computed(() => {
      const days = []
      const calendarDate = moment([selectedYear.value, selectedMonth.value - 1, 1])
      const monthStart = calendarDate.clone().startOf('month')
      const monthEnd = calendarDate.clone().endOf('month')
      const calendarStart = monthStart.clone().startOf('week')
      const calendarEnd = monthEnd.clone().endOf('week')
      
      // 檢查是否需要顯示六週（有些月份在日曆上可能需要六行）
      let daysInCalendar = calendarEnd.diff(calendarStart, 'days') + 1
      if (daysInCalendar < 42) {
        calendarEnd.add(7, 'days')
      }
      
      // 生成日曆的所有日期
      let day = calendarStart.clone()
      while (day.isSameOrBefore(calendarEnd)) {
        const isOtherMonth = day.month() !== monthStart.month()
        const dateStr = day.format('YYYY-MM-DD')
        const dayEvents = calendarStore.eventsByDate(dateStr)
        
        days.push({
          date: dateStr,
          dayNumber: day.date(),
          isOtherMonth,
          isToday: day.isSame(moment(), 'day'),
          events: dayEvents
        })
        
        day = day.add(1, 'day')
      }
      
      return days
    })
    
    const weekdays = ref(['日', '一', '二', '三', '四', '五', '六'])
    
    const months = ref([
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
    
    const years = computed(() => {
      const currentYear = moment().year()
      const yearOptions = []
      for (let i = currentYear - 5; i <= currentYear + 5; i++) {
        yearOptions.push(i)
      }
      return yearOptions
    })
    
    const currentMonthYear = computed(() => {
      return `${selectedYear.value}年 ${months.value.find(m => m.value === selectedMonth.value).name}`
    })
    
    // 安全等級選項
    const securityLevelOptions = computed(() => {
      if (!securityLevels.value) return []
      
      // 獲取所有安全等級
      const allLevels = Object.keys(securityLevels.value).map(level => ({
        name: securityLevels.value[level].description,
        value: level
      }))
      
      // 如果沒有當前用戶或沒有安全等級，返回所有選項
      if (!currentUser.value || !currentUser.value.securityLevel) return allLevels
      
      // 根據當前用戶安全等級過濾選項
      // 安全等級數字越小權限越高
      // LEVEL_1: 最高權限, LEVEL_2: 高級權限, LEVEL_3: 中級權限, LEVEL_4: 基本權限
      return allLevels.filter(option => {
        // 獲取選項等級的數字部分
        const optionLevelNum = parseInt(option.value.split('_')[1])
        // 獲取當前用戶等級的數字部分
        const userLevelNum = parseInt(currentUser.value.securityLevel.split('_')[1])
        // 只返回不高於當前用戶等級的選項（數字不小於用戶等級）
        return optionLevelNum >= userLevelNum
      })
    })
    
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
    
    // 獲取當前選中日期的事件
    const selectedDateEvents = computed(() => {
      if (!selectedDate.value) return []
      return calendarStore.eventsByDate(selectedDate.value)
    })
    
    // 表單驗證
    const validateEventForm = () => {
      if (!newEvent.value.title) {
        toast.add({
          severity: 'warn',
          summary: '提示',
          detail: '請輸入事件標題',
          life: 3000
        })
        return false
      }
      
      if (!newEvent.value.startTime || !newEvent.value.endTime) {
        toast.add({
          severity: 'warn',
          summary: '提示',
          detail: '請選擇開始和結束時間',
          life: 3000
        })
        return false
      }
      
      if (moment(newEvent.value.endTime).isBefore(moment(newEvent.value.startTime))) {
        toast.add({
          severity: 'warn',
          summary: '提示',
          detail: '結束時間不能早於開始時間',
          life: 3000
        })
        return false
      }
      
      return true
    }
    
    // 更新事件列表
    const updateEvents = async () => {
      try {
        await calendarStore.fetchMonthEvents()
      } catch (error) {
        console.error('獲取事件列表失敗:', error)
      }
    }
    
    // 加載初始數據
    const loadInitialData = async () => {
      isLoading.value = true
      try {
        // 設置當前日期
        const currentDate = `${selectedYear.value}-${String(selectedMonth.value).padStart(2, '0')}-01`;
        calendarStore.setCurrentDate(currentDate)
        
        // 加載本月事件
        await calendarStore.fetchMonthEvents()
        console.log('已載入的事件:', calendarStore.events)
        
        // 加載員工列表（用於事件共享）
        await employeeStore.fetchActiveEmployees()
      } catch (error) {
        console.error('加載日曆數據失敗:', error)
        toast.add({
          severity: 'error',
          summary: '錯誤',
          detail: '無法加載日曆數據',
          life: 3000
        })
      } finally {
        isLoading.value = false
      }
    }
    
    // 監聽選中的月份變化
    watch(
      [selectedMonth, selectedYear],
      async ([newMonth, newYear]) => {
        calendarStore.setCurrentDate(`${newYear}-${String(newMonth).padStart(2, '0')}-01`);
        await updateEvents();
      }
    );
    
    // 加載安全等級
    const loadSecurityLevels = async () => {
      try {
        await employeeStore.fetchSecurityLevels()
      } catch (error) {
        console.error('無法加載安全等級:', error)
      }
    }
    
    // 加載員工列表
    const loadEmployees = async () => {
      try {
        await employeeStore.fetchActiveEmployees()
      } catch (error) {
        console.error('無法加載員工列表:', error)
      }
    }
    
    // 導航到上一個月
    const previousMonth = () => {
      if (selectedMonth.value === 1) {
        selectedMonth.value = 12
        selectedYear.value--
      } else {
        selectedMonth.value--
      }
    }
    
    // 導航到下一個月
    const nextMonth = () => {
      if (selectedMonth.value === 12) {
        selectedMonth.value = 1
        selectedYear.value++
      } else {
        selectedMonth.value++
      }
    }
    
    // 導航到今天
    const goToToday = () => {
      selectedMonth.value = moment().month() + 1
      selectedYear.value = moment().year()
    }
    
    // 選擇日期
    const selectDay = (day) => {
      // 處理日期選擇，可以彈出該日事件列表等
      if (day.isOtherMonth) return
      
      // 設置新事件的時間為選擇的日期
      const selectedDate = moment(day.date)
      
      // 重置並打開新事件對話框
      resetNewEvent()
      newEvent.value.startTime = selectedDate.hour(9).minute(0).second(0).toDate()
      newEvent.value.endTime = selectedDate.hour(10).minute(0).second(0).toDate()
      
      // 如果有事件，可以顯示一個選擇菜單
      if (day.events && day.events.length > 0) {
        // 此處可以實現一個事件選擇功能
      } else {
        // 直接打開新事件對話框
        newEventDialog.value = true
      }
    }
    
    // 打開新事件對話框
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
      if (!validateEventForm()) return
      
      savingEvent.value = true
      
      try {
        const eventData = { ...newEvent.value }
        // 新增或更新事件
        if (isNewEvent.value) {
          await calendarStore.createEvent(eventData)
          toast.add({
            severity: 'success',
            summary: '成功',
            detail: '事件已成功創建',
            life: 3000
          })
        } else {
          await calendarStore.updateEventById({
            id: eventData.id,
            eventData
          })
          toast.add({
            severity: 'success',
            summary: '成功',
            detail: '事件已成功更新',
            life: 3000
          })
        }
        
        // 更新事件列表
        await updateEvents()
        
        // 關閉對話框
        newEventDialog.value = false
      } catch (error) {
        console.error('保存事件失敗:', error)
        toast.add({
          severity: 'error',
          summary: '錯誤',
          detail: '保存事件時發生錯誤',
          life: 3000
        })
      } finally {
        savingEvent.value = false
      }
    }
    
    // 打開事件詳情對話框
    const openEventDetailsDialog = async (event) => {
      selectedEvent.value = event
      
      // 獲取事件特殊權限
      if (isEventCreator.value) {
        try {
          await calendarStore.fetchEventPermissions(event.id)
        } catch (error) {
          console.error('無法獲取事件權限:', error)
        }
      }
      
      eventDetailsDialog.value = true
    }
    
    // 編輯事件
    const editEvent = () => {
      if (!selectedEvent.value) return
      
      // 複製事件對象進行編輯
      editingEvent.value = JSON.parse(JSON.stringify(selectedEvent.value))
      
      // 將字串日期轉換為Date物件
      editingEvent.value.startTime = new Date(editingEvent.value.startTime)
      editingEvent.value.endTime = new Date(editingEvent.value.endTime)
      
      // 關閉詳情對話框，打開編輯對話框
      eventDetailsDialog.value = false
      editEventDialog.value = true
    }
    
    // 保存編輯的事件
    const saveEditedEvent = async () => {
      if (!validateEventForm()) return
      
      savingEvent.value = true
      
      try {
        const eventData = { ...editingEvent.value }
        // 新增或更新事件
        if (isNewEvent.value) {
          await calendarStore.createEvent(eventData)
          toast.add({
            severity: 'success',
            summary: '成功',
            detail: '事件已成功創建',
            life: 3000
          })
        } else {
          await calendarStore.updateEventById({
            id: eventData.id,
            eventData
          })
          toast.add({
            severity: 'success',
            summary: '成功',
            detail: '事件已成功更新',
            life: 3000
          })
        }
        
        // 更新事件列表
        await updateEvents()
        
        // 關閉對話框
        editEventDialog.value = false
      } catch (error) {
        console.error('保存事件失敗:', error)
        toast.add({
          severity: 'error',
          summary: '錯誤',
          detail: '保存事件時發生錯誤',
          life: 3000
        })
      } finally {
        savingEvent.value = false
      }
    }
    
    // 確認刪除事件
    const confirmDeleteEvent = () => {
      confirm.require({
        message: '確定要刪除此事件嗎？',
        header: '刪除確認',
        icon: 'pi pi-exclamation-triangle',
        acceptLabel: '確定',
        rejectLabel: '取消',
        accept: () => deleteEvent()
      })
    }
    
    // 刪除事件
    const deleteEvent = async () => {
      if (!selectedEvent.value || !selectedEvent.value.id) return
      
      try {
        await calendarStore.deleteEvent(selectedEvent.value.id)
        
        toast.add({
          severity: 'success',
          summary: '成功',
          detail: '事件已成功刪除',
          life: 3000
        })
        
        // 更新事件列表
        await updateEvents()
        
        // 關閉對話框
        eventDetailsDialog.value = false
      } catch (error) {
        console.error('刪除事件失敗:', error)
        toast.add({
          severity: 'error',
          summary: '錯誤',
          detail: '刪除事件時發生錯誤',
          life: 3000
        })
      }
    }
    
    // 工具方法
    const formatEventTime = (event) => {
      const start = moment(event.startTime)
      return start.format('HH:mm')
    }
    
    const formatEventDateTime = (event) => {
      const start = moment(event.startTime)
      const end = moment(event.endTime)
      
      if (start.isSame(end, 'day')) {
        return `${start.format('YYYY/MM/DD HH:mm')} - ${end.format('HH:mm')}`
      } else {
        return `${start.format('YYYY/MM/DD HH:mm')} - ${end.format('YYYY/MM/DD HH:mm')}`
      }
    }
    
    const getEventClass = (event) => {
      if (!event.securityLevel) return ''
      
      switch (event.securityLevel) {
        case 'LEVEL_1': return 'event-level1'
        case 'LEVEL_2': return 'event-level2'
        case 'LEVEL_3': return 'event-level3'
        case 'LEVEL_4': return 'event-level4'
        default: return ''
      }
    }
    
    const getSecurityLevelName = (level) => {
      if (!securityLevels.value || !level) return level
      
      return securityLevels.value[level]?.description || level
    }
    
    // 計算屬性
    const hasEditPermission = computed(() => {
      if (!currentUser.value) return false
      // 最高權限可以編輯任何事件
      if (currentUser.value.securityLevel === 'LEVEL_1') return true
      // 事件創建者可以編輯自己的事件
      if (selectedEvent.value && currentUser.value.id === selectedEvent.value.createdBy) return true
      return false
    })
    
    // 初始化
    onMounted(async () => {
      try {
        console.log('Calendar 組件已掛載，開始加載數據')
        
        // 加載安全等級
        await loadSecurityLevels()
        
        // 加載員工列表
        await loadEmployees()
        
        // 加載初始數據
        await loadInitialData()
        
        console.log('所有數據加載完成', {
          events: calendarStore.events,
          currentMonth: calendarStore.currentMonth,
          currentYear: calendarStore.currentYear
        })
      } catch (error) {
        console.error('初始化日曆失敗:', error)
      }
    })
    
    return {
      // 日期相關
      selectedDate,
      selectedMonth,
      selectedYear,
      weekdays,
      months,
      years,
      currentMonthYear,
      calendarDays,
      
      // 對話框狀態
      newEventDialog,
      eventDetailsDialog,
      editEventDialog,
      
      // 事件相關
      newEvent,
      selectedEvent,
      editingEvent,
      savingEvent,
      securityLevelOptions,
      lowerLevelEmployees,
      specialViewersDisplay,
      
      // 計算屬性
      isEventCreator,
      canEditEvent,
      canDeleteEvent,
      canGrantSpecialPermissions,
      selectedDateEvents,
      employees,
      hasEditPermission,
      
      // 方法
      previousMonth,
      nextMonth,
      goToToday,
      selectDay,
      openNewEventDialog,
      resetNewEvent,
      saveNewEvent,
      openEventDetailsDialog,
      editEvent,
      saveEditedEvent,
      confirmDeleteEvent,
      deleteEvent,
      formatEventTime,
      formatEventDateTime,
      getEventClass,
      getSecurityLevelName
    }
  }
}
</script>

<style scoped>
.calendar-container {
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  padding: 1.5rem;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.calendar-header h1 {
  margin: 0;
  color: #3B82F6;
  font-size: 1.8rem;
}

.calendar-controls {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.month-dropdown, .year-dropdown {
  width: 120px;
  margin-left: 0.5rem;
}

.calendar-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
  margin-bottom: 4px;
}

.weekday {
  text-align: center;
  padding: 0.5rem;
  font-weight: 600;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.calendar-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.calendar-day {
  position: relative;
  min-height: 100px;
  background-color: #fff;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  padding: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s ease;
}

.calendar-day:hover {
  background-color: #f8f9fa;
  border-color: #d4d4d4;
}

.day-number {
  text-align: right;
  margin-bottom: 4px;
  font-weight: 600;
}

.other-month {
  opacity: 0.6;
  background-color: #f8f9fa;
}

.today {
  border: 2px solid #3B82F6;
}

.has-events .day-number {
  color: #3B82F6;
}

.day-events {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 0.8rem;
}

.day-event {
  padding: 2px 4px;
  border-radius: 3px;
  background-color: #e6f2ff;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  cursor: pointer;
  border-left: 3px solid #3B82F6;
  transition: all 0.2s ease;
}

.day-event:hover {
  transform: translateY(-1px);
}

.event-time {
  font-weight: 600;
  margin-right: 4px;
}

/* 不同安全等級的事件顏色 */
.event-level1 {
  background-color: #ffe6e6;
  border-left-color: #ff4d4d;
}

.event-level2 {
  background-color: #fff9e6;
  border-left-color: #ffcc00;
}

.event-level3 {
  background-color: #e6f9ff;
  border-left-color: #33ccff;
}

.event-level4 {
  background-color: #e6ffe6;
  border-left-color: #33cc33;
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

/* 響應式設計 */
@media (max-width: 768px) {
  .calendar-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .calendar-weekdays, .calendar-days {
    grid-template-columns: repeat(7, 1fr);
  }
  
  .calendar-day {
    min-height: 80px;
  }
}
</style>