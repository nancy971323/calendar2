<template>
  <div class="login-container">
    <Card class="login-card">
      <template #title>
        <h1 class="login-title">登入系統</h1>
      </template>
      <template #content>
        <div class="login-form">
          <Message v-if="errorMessage" severity="error" :closable="false">{{ errorMessage }}</Message>
          
          <div class="p-fluid">
            <div class="p-field">
              <label for="username">用戶名</label>
              <InputText 
                id="username" 
                v-model.trim="username" 
                placeholder="請輸入用戶名"
                :class="{ 'p-invalid': v$.username.$invalid && v$.username.$dirty }"
                @blur="v$.username.$touch()"
              />
              <small v-if="v$.username.$invalid && v$.username.$dirty" class="p-error">
                {{ v$.username.$errors[0].$message }}
              </small>
            </div>
            
            <div class="p-field">
              <label for="password">密碼</label>
              <Password 
                id="password" 
                v-model="password" 
                placeholder="請輸入密碼" 
                :feedback="false"
                toggleMask
                :class="{ 'p-invalid': v$.password.$invalid && v$.password.$dirty }"
                @blur="v$.password.$touch()"
              />
              <small v-if="v$.password.$invalid && v$.password.$dirty" class="p-error">
                {{ v$.password.$errors[0].$message }}
              </small>
            </div>
            
            <div class="login-actions">
              <Button 
                label="登入" 
                icon="pi pi-sign-in" 
                class="p-button-lg" 
                :loading="loading" 
                :disabled="loading" 
                @click="login"
              />
            </div>
          </div>
        </div>
      </template>
    </Card>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/store/modules/auth'
import { useToast } from 'primevue/usetoast'
import { useVuelidate } from '@vuelidate/core'
import { required, minLength } from '@vuelidate/validators'

export default {
  name: 'LoginComponent',
  
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()
    const route = useRoute()
    const toast = useToast()
    
    // 表單數據
    const username = ref('')
    const password = ref('')
    const loading = ref(false)
    const errorMessage = ref('')
    
    // 表單驗證規則
    const rules = {
      username: { required, minLength: minLength(3) },
      password: { required, minLength: minLength(6) }
    }
    
    const form = { username, password }
    
    const authError = computed(() => authStore.error)
    
    const v$ = useVuelidate(rules, form)
    
    // 登入方法
    const login = async () => {
      loading.value = true
      const result = await v$.value.$validate()
      
      if (result) {
        try {
          const user = await authStore.login({
            username: username.value,
            password: password.value
          })
          
          // 如果有重定向目標，則重定向過去，否則到首頁
          const redirectPath = route.query.redirect || '/'
          router.push(redirectPath)
          
          toast.add({
            severity: 'success',
            summary: '登入成功',
            detail: `歡迎回來，${user.fullName}`,
            life: 3000
          })
        } catch (error) {
          toast.add({
            severity: 'error',
            summary: '登入失敗',
            detail: authError.value || '帳號或密碼錯誤',
            life: 3000
          })
        } finally {
          loading.value = false
        }
      } else {
        loading.value = false
      }
    }
    
    return {
      username,
      password,
      loading,
      errorMessage,
      v$,
      login,
      authError
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 100px);
  padding: 2rem;
}

.login-card {
  width: 100%;
  max-width: 450px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.login-title {
  text-align: center;
  color: #3B82F6;
}

.login-form {
  margin-top: 1rem;
}

.p-field {
  margin-bottom: 1.5rem;
}

.p-field label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.login-actions {
  margin-top: 2rem;
  display: flex;
  justify-content: center;
}

.p-error {
  color: #f44336;
  font-size: 0.875rem;
}
</style>