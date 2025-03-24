<template>
  <div class="employee-management-container">
    <div class="employee-management-header">
      <h2>員工管理</h2>
      <Button label="新增員工" icon="pi pi-plus" @click="openNewEmployeeDialog" />
    </div>
    
    <div class="employee-management-content">
      <DataTable
        :value="employees"
        :paginator="true"
        :rows="10"
        :rowsPerPageOptions="[5, 10, 20, 50]"
        paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
        currentPageReportTemplate="顯示 {first} 到 {last} 共 {totalRecords} 筆記錄"
        responsiveLayout="scroll"
        stripedRows
        class="employee-table"
      >
        <Column field="id" header="ID" style="width: 5%"></Column>
        <Column field="username" header="用戶名" style="width: 15%"></Column>
        <Column field="fullName" header="姓名" style="width: 15%"></Column>
        <Column field="email" header="電子郵件" style="width: 20%"></Column>
        <Column field="department" header="部門" style="width: 15%"></Column>
        <Column field="securityLevel" header="安全等級" style="width: 15%">
          <template #body="slotProps">
            <SecurityLevelBadge :level="slotProps.data.securityLevel" />
          </template>
        </Column>
        <Column field="active" header="狀態" style="width: 5%">
          <template #body="slotProps">
            <span :class="slotProps.data.active ? 'status-active' : 'status-inactive'">
              {{ slotProps.data.active ? '啟用' : '停用' }}
            </span>
          </template>
        </Column>
        <Column header="操作" style="width: 10%">
          <template #body="slotProps">
            <Button icon="pi pi-pencil" class="p-button-rounded p-button-secondary p-button-sm" @click="editEmployee(slotProps.data)" />
            <Button icon="pi pi-trash" class="p-button-rounded p-button-danger p-button-sm" @click="confirmDeleteEmployee(slotProps.data)" />
          </template>
        </Column>
      </DataTable>
    </div>
    
    <!-- 新增員工對話框 -->
    <Dialog v-model:visible="newEmployeeDialog" header="新增員工" :style="{ width: '500px' }" :modal="true">
      <div class="p-fluid">
        <div class="p-field">
          <label for="username">用戶名</label>
          <InputText id="username" v-model.trim="newEmployee.username" placeholder="用戶名" />
        </div>
        
        <div class="p-field">
          <label for="password">密碼</label>
          <Password id="password" v-model="newEmployee.password" placeholder="密碼" toggleMask />
        </div>
        
        <div class="p-field">
          <label for="fullName">姓名</label>
          <InputText id="fullName" v-model.trim="newEmployee.fullName" placeholder="姓名" />
        </div>
        
        <div class="p-field">
          <label for="email">電子郵件</label>
          <InputText id="email" v-model.trim="newEmployee.email" placeholder="電子郵件" />
        </div>
        
        <div class="p-field">
          <label for="department">部門</label>
          <InputText id="department" v-model.trim="newEmployee.department" placeholder="部門" />
        </div>
        
        <div class="p-field">
          <label for="securityLevel">安全等級</label>
          <Dropdown id="securityLevel" v-model="newEmployee.securityLevel" :options="securityLevelOptions" optionLabel="name" optionValue="value" placeholder="選擇安全等級" />
        </div>
      </div>
      
      <template #footer>
        <Button label="取消" icon="pi pi-times" class="p-button-text" @click="newEmployeeDialog = false" />
        <Button label="儲存" icon="pi pi-check" @click="saveNewEmployee" :loading="savingEmployee" />
      </template>
    </Dialog>
    
    <!-- 編輯員工對話框 -->
    <Dialog v-model:visible="editEmployeeDialog" header="編輯員工" :style="{ width: '500px' }" :modal="true">
      <div class="p-fluid" v-if="editingEmployee">
        <div class="p-field">
          <label for="edit-username">用戶名</label>
          <InputText id="edit-username" v-model.trim="editingEmployee.username" placeholder="用戶名" />
        </div>
        
        <div class="p-field">
          <label for="edit-fullName">姓名</label>
          <InputText id="edit-fullName" v-model.trim="editingEmployee.fullName" placeholder="姓名" />
        </div>
        
        <div class="p-field">
          <label for="edit-email">電子郵件</label>
          <InputText id="edit-email" v-model.trim="editingEmployee.email" placeholder="電子郵件" />
        </div>
        
        <div class="p-field">
          <label for="edit-department">部門</label>
          <InputText id="edit-department" v-model.trim="editingEmployee.department" placeholder="部門" />
        </div>
        
        <div class="p-field">
          <label for="edit-securityLevel">安全等級</label>
          <Dropdown id="edit-securityLevel" v-model="editingEmployee.securityLevel" :options="securityLevelOptions" optionLabel="name" optionValue="value" placeholder="選擇安全等級" />
        </div>
        
        <div class="p-field">
          <label for="edit-active">狀態</label>
          <div class="p-formgrid p-grid">
            <div class="p-field-radiobutton p-col-6">
              <input type="radio" id="active" name="active" :value="true" v-model="editingEmployee.active" />
              <label for="active">啟用</label>
            </div>
            <div class="p-field-radiobutton p-col-6">
              <input type="radio" id="inactive" name="active" :value="false" v-model="editingEmployee.active" />
              <label for="inactive">停用</label>
            </div>
          </div>
        </div>
        
        <Divider />
        
        <div class="p-field">
          <label for="edit-password">重設密碼 (如不修改請留空)</label>
          <Password id="edit-password" v-model="editingEmployeePassword" placeholder="新密碼" toggleMask />
        </div>
      </div>
      
      <template #footer>
        <Button label="取消" icon="pi pi-times" class="p-button-text" @click="editEmployeeDialog = false" />
        <Button label="儲存" icon="pi pi-check" @click="saveEditedEmployee" :loading="savingEmployee" />
      </template>
    </Dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import SecurityLevelBadge from './shared/SecurityLevelBadge.vue'

export default {
  name: 'EmployeeManagement',
  
  components: {
    SecurityLevelBadge
  },
  
  setup() {
    const store = useStore()
    const toast = useToast()
    const confirm = useConfirm()
    
    // 員工列表
    const employees = computed(() => store.getters['employee/employees'])
    
    // 對話框狀態
    const newEmployeeDialog = ref(false)
    const editEmployeeDialog = ref(false)
    
    // 員工相關
    const newEmployee = ref({
      username: '',
      password: '',
      fullName: '',
      email: '',
      department: '',
      securityLevel: null
    })
    
    const editingEmployee = ref(null)
    const editingEmployeePassword = ref('')
    const savingEmployee = ref(false)
    
    // 安全等級選項
    const securityLevels = computed(() => store.getters['employee/securityLevels'])
    
    const securityLevelOptions = computed(() => {
      if (!securityLevels.value) return []
      
      return Object.keys(securityLevels.value).map(level => ({
        name: securityLevels.value[level].description,
        value: level
      }))
    })
    
    // 打開新增員工對話框
    const openNewEmployeeDialog = () => {
      resetNewEmployee()
      newEmployeeDialog.value = true
    }
    
    // 重置新員工表單
    const resetNewEmployee = () => {
      newEmployee.value = {
        username: '',
        password: '',
        fullName: '',
        email: '',
        department: '',
        securityLevel: null
      }
    }
    
    // 儲存新員工
    const saveNewEmployee = async () => {
      try {
        // 表單驗證
        if (!newEmployee.value.username) {
          toast.add({
            severity: 'warn',
            summary: '提示',
            detail: '請輸入用戶名',
            life: 3000
          })
          return
        }
        
        if (!newEmployee.value.password) {
          toast.add({
            severity: 'warn',
            summary: '提示',
            detail: '請輸入密碼',
            life: 3000
          })
          return
        }
        
        if (!newEmployee.value.fullName) {
          toast.add({
            severity: 'warn',
            summary: '提示',
            detail: '請輸入姓名',
            life: 3000
          })
          return
        }
        
        if (!newEmployee.value.email) {
          toast.add({
            severity: 'warn',
            summary: '提示',
            detail: '請輸入電子郵件',
            life: 3000
          })
          return
        }
        
        if (!newEmployee.value.securityLevel) {
          toast.add({
            severity: 'warn',
            summary: '提示',
            detail: '請選擇安全等級',
            life: 3000
          })
          return
        }
        
        savingEmployee.value = true
        
        // 創建新員工
        await store.dispatch('employee/createEmployee', {
          ...newEmployee.value,
          active: true
        })
        
        toast.add({
          severity: 'success',
          summary: '成功',
          detail: '員工已創建',
          life: 3000
        })
        
        newEmployeeDialog.value = false
        resetNewEmployee()
        
        // 重新加載員工列表
        await store.dispatch('employee/fetchActiveEmployees')
      } catch (error) {
        toast.add({
          severity: 'error',
          summary: '錯誤',
          detail: error.response?.data || '創建員工失敗',
          life: 3000
        })
      } finally {
        savingEmployee.value = false
      }
    }
    
    // 編輯員工
    const editEmployee = (employee) => {
      editingEmployee.value = JSON.parse(JSON.stringify(employee))
      editingEmployeePassword.value = ''
      editEmployeeDialog.value = true
    }
    
    // 儲存編輯的員工
    const saveEditedEmployee = async () => {
      try {
        // 表單驗證
        if (!editingEmployee.value.username) {
          toast.add({
            severity: 'warn',
            summary: '提示',
            detail: '請輸入用戶名',
            life: 3000
          })
          return
        }
        
        if (!editingEmployee.value.fullName) {
          toast.add({
            severity: 'warn',
            summary: '提示',
            detail: '請輸入姓名',
            life: 3000
          })
          return
        }
        
        if (!editingEmployee.value.email) {
          toast.add({
            severity: 'warn',
            summary: '提示',
            detail: '請輸入電子郵件',
            life: 3000
          })
          return
        }
        
        if (!editingEmployee.value.securityLevel) {
          toast.add({
            severity: 'warn',
            summary: '提示',
            detail: '請選擇安全等級',
            life: 3000
          })
          return
        }
        
        savingEmployee.value = true
        
        // 更新員工信息
        await store.dispatch('employee/updateEmployee', {
          id: editingEmployee.value.id,
          employeeData: editingEmployee.value
        })
        
        // 如果有新密碼，更新密碼
        if (editingEmployeePassword.value) {
          await store.dispatch('employee/updateEmployeePassword', {
            id: editingEmployee.value.id,
            password: editingEmployeePassword.value
          })
        }
        
        toast.add({
          severity: 'success',
          summary: '成功',
          detail: '員工已更新',
          life: 3000
        })
        
        editEmployeeDialog.value = false
        editingEmployee.value = null
        editingEmployeePassword.value = ''
        
        // 重新加載員工列表
        await store.dispatch('employee/fetchActiveEmployees')
      } catch (error) {
        toast.add({
          severity: 'error',
          summary: '錯誤',
          detail: error.response?.data || '更新員工失敗',
          life: 3000
        })
      } finally {
        savingEmployee.value = false
      }
    }
    
    // 確認刪除員工
    const confirmDeleteEmployee = (employee) => {
      confirm.require({
        message: `確定要刪除員工 "${employee.fullName}" 嗎？`,
        header: '刪除確認',
        icon: 'pi pi-exclamation-triangle',
        acceptLabel: '確定',
        rejectLabel: '取消',
        accept: () => deleteEmployee(employee)
      })
    }
    
    // 刪除員工
    const deleteEmployee = async (employee) => {
      try {
        await store.dispatch('employee/deleteEmployee', employee.id)
        
        toast.add({
          severity: 'success',
          summary: '成功',
          detail: '員工已刪除',
          life: 3000
        })
        
        // 重新加載員工列表
        await store.dispatch('employee/fetchActiveEmployees')
      } catch (error) {
        toast.add({
          severity: 'error',
          summary: '錯誤',
          detail: error.response?.data || '刪除員工失敗',
          life: 3000
        })
      }
    }
    
    // 初始化
    onMounted(async () => {
      // 加載安全等級
      try {
        await store.dispatch('employee/fetchSecurityLevels')
      } catch (error) {
        console.error('無法加載安全等級:', error)
      }
      
      // 加載員工列表
      try {
        await store.dispatch('employee/fetchActiveEmployees')
      } catch (error) {
        console.error('無法加載員工列表:', error)
      }
    })
    
    return {
      employees,
      newEmployeeDialog,
      editEmployeeDialog,
      newEmployee,
      editingEmployee,
      editingEmployeePassword,
      savingEmployee,
      securityLevelOptions,
      openNewEmployeeDialog,
      saveNewEmployee,
      editEmployee,
      saveEditedEmployee,
      confirmDeleteEmployee
    }
  }
}
</script>

<style scoped>
.employee-management-container {
  padding: 1rem 0;
}

.employee-management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.employee-management-header h2 {
  margin: 0;
  color: #3B82F6;
}

.employee-table {
  margin-top: 1rem;
}

/* 狀態樣式 */
.status-active {
  background-color: #c8e6c9;
  color: #2e7d32;
  font-weight: bold;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
}

.status-inactive {
  background-color: #ffcdd2;
  color: #c62828;
  font-weight: bold;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
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

.p-field {
  margin-bottom: 1.5rem;
}

.p-field-radiobutton {
  margin-right: 1rem;
  display: inline-flex;
  align-items: center;
}

.p-field-radiobutton label {
  margin-left: 0.5rem;
}
</style>