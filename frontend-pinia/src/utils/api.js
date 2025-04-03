import axios from 'axios'

// 創建 axios 實例
const api = axios.create({
  baseURL: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000
})

// 請求攔截器 - 添加 auth token
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 響應攔截器 - 處理常見錯誤
api.interceptors.response.use(
  response => {
    return response
  },
  error => {
    // 處理 401 未授權錯誤
    if (error.response && error.response.status === 401) {
      // 清除本地存儲的 token
      localStorage.removeItem('token')
      
      // 如果不是登入頁面，重定向到登入頁
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    
    return Promise.reject(error)
  }
)

export default api