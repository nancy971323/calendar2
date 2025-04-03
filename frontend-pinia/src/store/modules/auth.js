import { defineStore } from 'pinia'
import api from '../../utils/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    currentUser: null,
    error: null
  }),
  
  getters: {
    isAuthenticated: (state) => !!state.token && !!state.currentUser,
    hasHighestAuth: (state) => state.currentUser && state.currentUser.securityLevel === 'LEVEL_1',
    authError: (state) => state.error
  },
  
  actions: {
    // 設置認證令牌
    setAuthToken(token) {
      this.token = token
    },
    
    // 設置當前用戶
    setCurrentUser(user) {
      this.currentUser = user
    },
    
    // 設置認證錯誤
    setAuthError(error) {
      this.error = error
    },
    
    // 清除認證信息
    clearAuth() {
      this.token = null
      this.currentUser = null
      this.error = null
    },
    
    // 登入
    async login(credentials) {
      try {
        const response = await api.post('/api/auth/login', credentials)
        const { token, employee } = response.data
        
        // 存儲token和用戶信息
        localStorage.setItem('token', token)
        this.setAuthToken(token)
        this.setCurrentUser(employee)
        this.setAuthError(null)
        
        return employee
      } catch (error) {
        this.setAuthError(error.response?.data || '登入失敗')
        throw error
      }
    },
    
    // 登出
    async logout() {
      try {
        await api.post('/api/auth/logout')
      } catch (error) {
        console.error('登出API請求失敗:', error)
      } finally {
        // 無論API成功與否，都清除本地身份驗證狀態
        localStorage.removeItem('token')
        this.clearAuth()
      }
    },
    
    // 檢查身份驗證狀態
    async checkAuth() {
      if (!this.token) {
        this.clearAuth()
        return
      }
      
      try {
        const response = await api.get('/api/auth/current')
        this.setCurrentUser(response.data)
      } catch (error) {
        console.error('檢查身份驗證失敗:', error)
        localStorage.removeItem('token')
        this.clearAuth()
        throw error
      }
    }
  }
}) 