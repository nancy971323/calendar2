import { defineStore } from 'pinia'
import api from '../../utils/api'

export const useEmployeeStore = defineStore('employee', {
  state: () => ({
    employees: [],
    currentEmployee: null,
    securityLevels: null
  }),
  
  getters: {
    // 取得指定安全等級的員工列表
    employeesBySecurityLevel: (state) => (level) => {
      return state.employees.filter(employee => employee.securityLevel === level)
    },
    
    // 取得員工名稱對應表 (id -> fullName)
    employeeNameMap: (state) => {
      const map = {}
      state.employees.forEach(employee => {
        map[employee.id] = employee.fullName
      })
      return map
    }
  },
  
  actions: {
    // 設置員工列表
    setEmployees(employees) {
      this.employees = employees
    },
    
    // 設置當前員工
    setEmployee(employee) {
      this.currentEmployee = employee
    },
    
    // 添加員工
    addEmployee(employee) {
      this.employees.push(employee)
    },
    
    // 更新員工
    updateEmployee(updatedEmployee) {
      const index = this.employees.findIndex(e => e.id === updatedEmployee.id)
      if (index !== -1) {
        this.employees.splice(index, 1, updatedEmployee)
      }
      if (this.currentEmployee && this.currentEmployee.id === updatedEmployee.id) {
        this.currentEmployee = updatedEmployee
      }
    },
    
    // 移除員工
    removeEmployee(id) {
      this.employees = this.employees.filter(employee => employee.id !== id)
      if (this.currentEmployee && this.currentEmployee.id === id) {
        this.currentEmployee = null
      }
    },
    
    // 設置安全等級
    setSecurityLevels(securityLevels) {
      this.securityLevels = securityLevels
    },
    
    // 獲取所有員工
    async fetchEmployees() {
      try {
        const response = await api.get('/api/employees')
        this.setEmployees(response.data)
        return response.data
      } catch (error) {
        console.error('獲取員工列表失敗:', error)
        throw error
      }
    },
    
    // 獲取所有活躍員工
    async fetchActiveEmployees() {
      try {
        const response = await api.get('/api/employees/active')
        this.setEmployees(response.data)
        return response.data
      } catch (error) {
        console.error('獲取活躍員工列表失敗:', error)
        throw error
      }
    },
    
    // 獲取員工詳情
    async fetchEmployee(id) {
      try {
        const response = await api.get(`/api/employees/${id}`)
        this.setEmployee(response.data)
        return response.data
      } catch (error) {
        console.error('獲取員工詳情失敗:', error)
        throw error
      }
    },
    
    // 創建員工
    async createEmployee(employeeData) {
      try {
        const response = await api.post('/api/employees', employeeData)
        this.addEmployee(response.data)
        return response.data
      } catch (error) {
        console.error('創建員工失敗:', error)
        throw error
      }
    },
    
    // 更新員工
    async updateEmployeeById({ id, employeeData }) {
      try {
        const response = await api.put(`/api/employees/${id}`, employeeData)
        this.updateEmployee(response.data)
        return response.data
      } catch (error) {
        console.error('更新員工失敗:', error)
        throw error
      }
    },
    
    // 更新員工密碼
    async updateEmployeePassword({ id, password }) {
      try {
        await api.put(`/api/employees/${id}/password`, { password })
        return true
      } catch (error) {
        console.error('更新員工密碼失敗:', error)
        throw error
      }
    },
    
    // 刪除員工
    async deleteEmployee(id) {
      try {
        await api.delete(`/api/employees/${id}`)
        this.removeEmployee(id)
        return true
      } catch (error) {
        console.error('刪除員工失敗:', error)
        throw error
      }
    },
    
    // 獲取安全等級列表
    async fetchSecurityLevels() {
      try {
        const response = await api.get('/api/employees/security-levels')
        this.setSecurityLevels(response.data)
        return response.data
      } catch (error) {
        console.error('獲取安全等級列表失敗:', error)
        throw error
      }
    },
    
    // 根據部門獲取員工
    async fetchEmployeesByDepartment(department) {
      try {
        const response = await api.get(`/api/employees/department/${department}`)
        this.setEmployees(response.data)
        return response.data
      } catch (error) {
        console.error('獲取部門員工列表失敗:', error)
        throw error
      }
    },
    
    // 根據安全等級獲取員工
    async fetchEmployeesBySecurityLevel(level) {
      try {
        const response = await api.get(`/api/employees/security-level/${level}`)
        this.setEmployees(response.data)
        return response.data
      } catch (error) {
        console.error('獲取安全等級員工列表失敗:', error)
        throw error
      }
    },
    
    // 獲取安全等級低於指定等級的員工
    async fetchEmployeesWithLowerSecurityLevel(level) {
      try {
        const response = await api.get(`/api/employees/lower-security-level/${level}`)
        this.setEmployees(response.data)
        return response.data
      } catch (error) {
        console.error('獲取低安全等級員工列表失敗:', error)
        throw error
      }
    }
  }
}) 