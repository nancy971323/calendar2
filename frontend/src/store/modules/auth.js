import { 
    SET_CURRENT_USER, 
    SET_AUTH_TOKEN, 
    SET_AUTH_ERROR, 
    CLEAR_AUTH 
  } from '../mutation-types'
  import api from '../../utils/api'
  
  // 初始狀態
  const state = {
    token: localStorage.getItem('token') || null,
    currentUser: null,
    error: null
  }
  
  // 獲取器
  const getters = {
    isAuthenticated: state => !!state.token && !!state.currentUser,
    currentUser: state => state.currentUser,
    authError: state => state.error,
    hasHighestAuth: state => state.currentUser && state.currentUser.securityLevel === 'LEVEL_1'
  }
  
  // 動作
  const actions = {
    // 登入
    async login({ commit }, credentials) {
      try {
        const response = await api.post('/api/auth/login', credentials)
        const { token, employee } = response.data
        
        // 存儲token和用戶信息
        localStorage.setItem('token', token)
        commit(SET_AUTH_TOKEN, token)
        commit(SET_CURRENT_USER, employee)
        commit(SET_AUTH_ERROR, null)
        
        return employee
      } catch (error) {
        commit(SET_AUTH_ERROR, error.response?.data || '登入失敗')
        throw error
      }
    },
    
    // 登出
    async logout({ commit }) {
      try {
        await api.post('/api/auth/logout')
      } catch (error) {
        console.error('登出API請求失敗:', error)
      } finally {
        // 無論API成功與否，都清除本地身份驗證狀態
        localStorage.removeItem('token')
        commit(CLEAR_AUTH)
      }
    },
    
    // 檢查身份驗證狀態
    async checkAuth({ commit, state }) {
      if (!state.token) {
        commit(CLEAR_AUTH)
        return
      }
      
      try {
        const response = await api.get('/api/auth/me')
        commit(SET_CURRENT_USER, response.data)
      } catch (error) {
        console.error('檢查身份驗證失敗:', error)
        localStorage.removeItem('token')
        commit(CLEAR_AUTH)
        throw error
      }
    }
  }
  
  // 變異
  const mutations = {
    [SET_AUTH_TOKEN](state, token) {
      state.token = token
    },
    [SET_CURRENT_USER](state, user) {
      state.currentUser = user
    },
    [SET_AUTH_ERROR](state, error) {
      state.error = error
    },
    [CLEAR_AUTH](state) {
      state.token = null
      state.currentUser = null
      state.error = null
    }
  }
  
  export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
  }