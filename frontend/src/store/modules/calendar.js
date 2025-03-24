import { 
    SET_EVENTS, 
    SET_EVENT, 
    ADD_EVENT, 
    UPDATE_EVENT, 
    REMOVE_EVENT,
    SET_CURRENT_DATE,
    SET_EVENT_PERMISSIONS
  } from '../mutation-types'
  import api from '../../utils/api'
  import moment from 'moment'
  
  // 初始狀態
  const state = {
    events: [],
    currentEvent: null,
    currentDate: moment().format('YYYY-MM-DD'),
    currentMonth: moment().month() + 1,
    currentYear: moment().year(),
    eventPermissions: {}
  }
  
  // 獲取器
  const getters = {
    events: state => state.events,
    currentEvent: state => state.currentEvent,
    currentDate: state => state.currentDate,
    currentMonth: state => state.currentMonth,
    currentYear: state => state.currentYear,
    // 獲取指定日期的事件
    eventsByDate: state => date => {
      const dateStr = moment(date).format('YYYY-MM-DD')
      return state.events.filter(event => {
        const startDate = moment(event.startTime).format('YYYY-MM-DD')
        const endDate = moment(event.endTime).format('YYYY-MM-DD')
        
        // 檢查事件是否在指定日期當天或跨越該日期
        return moment(dateStr).isBetween(startDate, endDate, null, '[]')
      })
    },
    // 獲取事件的特殊查看權限
    eventViewers: state => eventId => {
      return state.eventPermissions[eventId] || []
    }
  }
  
  // 動作
  const actions = {
    // 設置當前日期
    setCurrentDate({ commit }, date) {
      commit(SET_CURRENT_DATE, date)
    },
    
    // 獲取當月事件
    async fetchMonthEvents({ commit, state }) {
      try {
        const response = await api.get(`/api/calendar/events/visible/year/${state.currentYear}/month/${state.currentMonth}`)
        commit(SET_EVENTS, response.data)
        return response.data
      } catch (error) {
        console.error('獲取當月事件失敗:', error)
        throw error
      }
    },
    
    // 獲取事件詳情
    async fetchEvent({ commit }, id) {
      try {
        const response = await api.get(`/api/calendar/events/${id}`)
        commit(SET_EVENT, response.data)
        return response.data
      } catch (error) {
        console.error('獲取事件詳情失敗:', error)
        throw error
      }
    },
    
    // 創建事件
    async createEvent({ commit }, eventData) {
      try {
        const response = await api.post('/api/calendar/events', eventData)
        commit(ADD_EVENT, response.data)
        return response.data
      } catch (error) {
        console.error('創建事件失敗:', error)
        throw error
      }
    },
    
    // 更新事件
    async updateEvent({ commit }, { id, eventData }) {
      try {
        const response = await api.put(`/api/calendar/events/${id}`, eventData)
        commit(UPDATE_EVENT, response.data)
        return response.data
      } catch (error) {
        console.error('更新事件失敗:', error)
        throw error
      }
    },
    
    // 刪除事件
    async deleteEvent({ commit }, id) {
      try {
        await api.delete(`/api/calendar/events/${id}`)
        commit(REMOVE_EVENT, id)
        return true
      } catch (error) {
        console.error('刪除事件失敗:', error)
        throw error
      }
    },
    
    // 獲取事件的特殊查看權限
    async fetchEventPermissions({ commit }, eventId) {
      try {
        const response = await api.get(`/api/calendar/events/${eventId}/permissions`)
        commit(SET_EVENT_PERMISSIONS, { eventId, permissions: response.data })
        return response.data
      } catch (error) {
        console.error('獲取事件權限失敗:', error)
        throw error
      }
    },
    
    // 添加事件查看權限
    async addEventPermission({ dispatch }, { eventId, employeeId }) {
      try {
        await api.post(`/api/calendar/events/${eventId}/permissions/${employeeId}`)
        // 重新獲取權限列表
        await dispatch('fetchEventPermissions', eventId)
        return true
      } catch (error) {
        console.error('添加事件權限失敗:', error)
        throw error
      }
    },
    
    // 移除事件查看權限
    async removeEventPermission({ dispatch }, { eventId, employeeId }) {
      try {
        await api.delete(`/api/calendar/events/${eventId}/permissions/${employeeId}`)
        // 重新獲取權限列表
        await dispatch('fetchEventPermissions', eventId)
        return true
      } catch (error) {
        console.error('移除事件權限失敗:', error)
        throw error
      }
    },
    
    // 檢查是否可以查看事件
    async canViewEvent({ _ }, eventId) {
      try {
        const response = await api.get(`/api/calendar/events/${eventId}/can-view`)
        return response.data
      } catch (error) {
        console.error('檢查事件權限失敗:', error)
        return false
      }
    }
  }
  
  // 變異
  const mutations = {
    [SET_EVENTS](state, events) {
      state.events = events
    },
    [SET_EVENT](state, event) {
      state.currentEvent = event
    },
    [ADD_EVENT](state, event) {
      state.events.push(event)
    },
    [UPDATE_EVENT](state, updatedEvent) {
      const index = state.events.findIndex(e => e.id === updatedEvent.id)
      if (index !== -1) {
        state.events.splice(index, 1, updatedEvent)
      }
      if (state.currentEvent && state.currentEvent.id === updatedEvent.id) {
        state.currentEvent = updatedEvent
      }
    },
    [REMOVE_EVENT](state, id) {
      state.events = state.events.filter(event => event.id !== id)
      if (state.currentEvent && state.currentEvent.id === id) {
        state.currentEvent = null
      }
    },
    [SET_CURRENT_DATE](state, date) {
      const momentDate = moment(date)
      state.currentDate = momentDate.format('YYYY-MM-DD')
      state.currentMonth = momentDate.month() + 1
      state.currentYear = momentDate.year()
    },
    [SET_EVENT_PERMISSIONS](state, { eventId, permissions }) {
      state.eventPermissions = {
        ...state.eventPermissions,
        [eventId]: permissions
      }
    }
  }
  
  export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
  }