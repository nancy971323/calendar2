// 檢查身份驗證狀態
export const isAuthenticated = () => {
    return !!localStorage.getItem('token')
  }
  
  // 獲取身份驗證令牌
  export const getToken = () => {
    return localStorage.getItem('token')
  }
  
  // 設置身份驗證令牌
  export const setToken = (token) => {
    localStorage.setItem('token', token)
  }
  
  // 清除身份驗證令牌
  export const clearToken = () => {
    localStorage.removeItem('token')
  }
  
  // 解析JWT令牌獲取信息（無需後端驗證）
  export const parseJwt = (token) => {
    try {
      const base64Url = token.split('.')[1]
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map(c => {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
          })
          .join('')
      )
      return JSON.parse(jsonPayload)
    } catch (e) {
      return null
    }
  }
  
  // 檢查令牌是否過期（前端檢查，無安全保證）
  export const isTokenExpired = (token) => {
    if (!token) return true
    
    const payload = parseJwt(token)
    if (!payload || !payload.exp) return true
    
    // 將過期時間戳（秒）轉換為毫秒
    const expirationDate = new Date(payload.exp * 1000)
    return expirationDate < new Date()
  }