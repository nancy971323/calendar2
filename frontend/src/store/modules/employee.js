import { 
    SET_EMPLOYEES, 
    SET_EMPLOYEE, 
    ADD_EMPLOYEE, 
    UPDATE_EMPLOYEE, 
    REMOVE_EMPLOYEE,
    SET_SECURITY_LEVELS
  } from '../mutation-types'
  import api from '../../utils/api'
  
  // 初始狀態
  const state = {
    employees: [],
    currentEmployee: null,
    securityLevels: null
  }
  
  // 獲取器
  const getters = {
    employees: state => state.employees,
    currentEmployee: state => state.currentEmployee,
    securityLevels: state => state.securityLevels,
    // 取得指定安全等級的員工列表
    employeesBySecurityLevel: state => level => {
      return state.employees.filter(employee => employee.securityLevel === level)
    },
    // 取得員工名稱對應表 (id -> fullName)
    employeeNameMap: state => {
      const map = {}
      state.employees.forEach(employee => {
        map[employee.id] = employee.fullName
      })
      return map
    }
  }
  
  // 動作
  const actions = {
    // 獲取所有員工
    async fetchEmployees({ commit }) {
      try {
        const response = await api.get('/api/employees')
        commit(SET_EMPLOYEES, response.data)
        return response.data
      } catch (error) {
        console.error('獲取員工列表失敗:', error)
        throw error
      }
    },
    
    // 獲取所有活躍員工
    async fetchActiveEmployees({ commit }) {
      try {
        const response = await api.get('/api/employees/active')
        commit(SET_EMPLOYEES, response.data)
        return response.data
      } catch (error) {
        console.error('獲取活躍員工列表失敗:', error)
        throw error
      }
    },
    
    // 獲取員工詳情
    async fetchEmployee({ commit }, id) {
      try {
        const response = await api.get(`/api/employees/${id}`)
        commit(SET_EMPLOYEE, response.data)
        return response.data
      } catch (error) {
        console.error('獲取員工詳情失敗:', error)
        throw error
      }
    },
    
    // 創建員工
    async createEmployee({ commit }, employeeData) {
      try {
        const response = await api.post('/api/employees', employeeData)
        commit(ADD_EMPLOYEE, response.data)
        return response.data
      } catch (error) {
        console.error('創建員工失敗:', error)
        throw error
      }
    },
    
    // 更新員工
    async updateEmployee({ commit }, { id, employeeData }) {
      try {
        const response = await api.put(`/api/employees/${id}`, employeeData)
        commit(UPDATE_EMPLOYEE, response.data)
        return response.data
      } catch (error) {
        console.error('更新員工失敗:', error)
        throw error
      }
    },
    
    // 更新員工密碼
    async updateEmployeePassword({ _ }, { id, password }) {
      try {
        await api.put(`/api/employees/${id}/password`, { password })
        return true
      } catch (error) {
        console.error('更新員工密碼失敗:', error)
        throw error
      }
    },
    
    // 刪除員工
    async deleteEmployee({ commit }, id) {
      try {
        await api.delete(`/api/employees/${id}`)
        commit(REMOVE_EMPLOYEE, id)
        return true
      } catch (error) {
        console.error('刪除員工失敗:', error)
        throw error
      }
    },
    
    // 獲取安全等級列表
    async fetchSecurityLevels({ commit }) {
      try {
        const response = await api.get('/api/employees/security-levels')
        commit(SET_SECURITY_LEVELS, response.data)
        return response.data
      } catch (error) {
        console.error('獲取安全等級列表失敗:', error)
        throw error
      }
    },
    
    // 根據部門獲取員工
    async fetchEmployeesByDepartment({ commit }, department) {
      try {
        const response = await api.get(`/api/employees/department/${department}`)
        commit(SET_EMPLOYEES, response.data)
        return response.data
      } catch (error) {
        console.error('獲取部門員工列表失敗:', error)
        throw error
      }
    },
    
    // 根據安全等級獲取員工
    async fetchEmployeesBySecurityLevel({ commit }, level) {
      try {
        const response = await api.get(`/api/employees/security-level/${level}`)
        commit(SET_EMPLOYEES, response.data)
        return response.data
      } catch (error) {
        console.error('獲取安全等級員工列表失敗:', error)
        throw error
      }
    },
    
    // 獲取安全等級低於指定等級的員工
    async fetchEmployeesWithLowerSecurityLevel({ commit }, level) {
      try {
        const response = await api.get(`/api/employees/lower-security-level/${level}`)
        commit(SET_EMPLOYEES, response.data)
        return response.data
      } catch (error) {
        console.error('獲取低安全等級員工列表失敗:', error)
        throw error
      }
    }
  }
  
  // 變異
  const mutations = {
    [SET_EMPLOYEES](state, employees) {
      state.employees = employees
    },
    [SET_EMPLOYEE](state, employee) {
      state.currentEmployee = employee
    },
    [ADD_EMPLOYEE](state, employee) {
      state.employees.push(employee)
    },
    [UPDATE_EMPLOYEE](state, updatedEmployee) {
      const index = state.employees.findIndex(e => e.id === updatedEmployee.id)
      if (index !== -1) {
        state.employees.splice(index, 1, updatedEmployee)
      }
      if (state.currentEmployee && state.currentEmployee.id === updatedEmployee.id) {
        state.currentEmployee = updatedEmployee
      }
    },
    [REMOVE_EMPLOYEE](state, id) {
      state.employees = state.employees.filter(employee => employee.id !== id)
      if (state.currentEmployee && state.currentEmployee.id === id) {
        state.currentEmployee = null
      }
    },
    [SET_SECURITY_LEVELS](state, securityLevels) {
      state.securityLevels = securityLevels
    }
  }
  
  export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
  }