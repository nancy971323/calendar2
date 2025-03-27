import { defineStore } from 'pinia'
import api from '../../utils/api'
import moment from 'moment'

export const useCalendarStore = defineStore('calendar', {
  state: () => ({
    events: [],
    currentEvent: null,
    currentDate: moment().format('YYYY-MM-DD'),
    currentMonth: moment().month() + 1,
    currentYear: moment().year(),
    eventPermissions: {}
  }),
  
  getters: {
    eventsByDate: (state) => (date) => {
      const dateStr = moment(date).format('YYYY-MM-DD')
      return state.events.filter(event => {
        const startDate = moment(event.startTime).format('YYYY-MM-DD')
        const endDate = moment(event.endTime).format('YYYY-MM-DD')
        
        // 檢查事件是否在指定日期當天或跨越該日期
        return moment(dateStr).isBetween(startDate, endDate, null, '[]')
      })
    },
    
    // 獲取事件的特殊查看權限
    eventViewers: (state) => (eventId) => {
      return state.eventPermissions[eventId] || []
    }
  },
  
  actions: {
    // 設置當前日期
    setCurrentDate(date) {
      const momentDate = moment(date)
      this.currentDate = momentDate.format('YYYY-MM-DD')
      this.currentMonth = momentDate.month() + 1
      this.currentYear = momentDate.year()
    },
    
    // 設置事件列表
    setEvents(events) {
      this.events = events
    },
    
    // 設置當前事件
    setEvent(event) {
      this.currentEvent = event
    },
    
    // 添加事件
    addEvent(event) {
      this.events.push(event)
    },
    
    // 更新事件
    updateEvent(updatedEvent) {
      const index = this.events.findIndex(e => e.id === updatedEvent.id)
      if (index !== -1) {
        this.events.splice(index, 1, updatedEvent)
      }
      if (this.currentEvent && this.currentEvent.id === updatedEvent.id) {
        this.currentEvent = updatedEvent
      }
    },
    
    // 移除事件
    removeEvent(id) {
      this.events = this.events.filter(event => event.id !== id)
      if (this.currentEvent && this.currentEvent.id === id) {
        this.currentEvent = null
      }
    },
    
    // 設置事件權限
    setEventPermissions({ eventId, permissions }) {
      this.eventPermissions = {
        ...this.eventPermissions,
        [eventId]: permissions
      }
    },
    
    // 獲取當月事件
    async fetchMonthEvents() {
      try {
        const response = await api.get(`/api/calendar/events/visible/year/${this.currentYear}/month/${this.currentMonth}`)
        this.setEvents(response.data)
        return response.data
      } catch (error) {
        console.error('獲取當月事件失敗:', error)
        throw error
      }
    },
    
    // 獲取事件詳情
    async fetchEvent(id) {
      try {
        const response = await api.get(`/api/calendar/events/${id}`)
        this.setEvent(response.data)
        return response.data
      } catch (error) {
        console.error('獲取事件詳情失敗:', error)
        throw error
      }
    },
    
    // 創建事件
    async createEvent(eventData) {
      try {
        const response = await api.post('/api/calendar/events', eventData)
        this.addEvent(response.data)
        return response.data
      } catch (error) {
        console.error('創建事件失敗:', error)
        throw error
      }
    },
    
    // 更新事件
    async updateEventById({ id, eventData }) {
      try {
        const response = await api.put(`/api/calendar/events/${id}`, eventData)
        this.updateEvent(response.data)
        return response.data
      } catch (error) {
        console.error('更新事件失敗:', error)
        throw error
      }
    },
    
    // 刪除事件
    async deleteEvent(id) {
      try {
        await api.delete(`/api/calendar/events/${id}`)
        this.removeEvent(id)
        return true
      } catch (error) {
        console.error('刪除事件失敗:', error)
        throw error
      }
    },
    
    // 獲取事件的特殊查看權限
    async fetchEventPermissions(eventId) {
      try {
        const response = await api.get(`/api/calendar/events/${eventId}/permissions`)
        this.setEventPermissions({ eventId, permissions: response.data })
        return response.data
      } catch (error) {
        console.error('獲取事件權限失敗:', error)
        throw error
      }
    },
    
    // 添加事件查看權限
    async addEventPermission({ eventId, employeeId }) {
      try {
        await api.post(`/api/calendar/events/${eventId}/permissions/${employeeId}`)
        // 重新獲取權限列表
        await this.fetchEventPermissions(eventId)
        return true
      } catch (error) {
        console.error('添加事件權限失敗:', error)
        throw error
      }
    },
    
    // 移除事件查看權限
    async removeEventPermission({ eventId, employeeId }) {
      try {
        await api.delete(`/api/calendar/events/${eventId}/permissions/${employeeId}`)
        // 重新獲取權限列表
        await this.fetchEventPermissions(eventId)
        return true
      } catch (error) {
        console.error('移除事件權限失敗:', error)
        throw error
      }
    },
    
    // 檢查是否可以查看事件
    async canViewEvent(eventId) {
      try {
        const response = await api.get(`/api/calendar/events/${eventId}/can-view`)
        return response.data
      } catch (error) {
        console.error('檢查事件權限失敗:', error)
        return false
      }
    }
  }
}) 