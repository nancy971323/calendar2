<template>
  <div class="admin-view">
    <div v-if="!hasHighestAuth" class="access-denied">
      <h1>訪問被拒絕</h1>
      <p>您沒有足夠的權限訪問此頁面，此頁面僅限最高權限用戶使用。</p>
      <Button @click="$router.push('/')" label="返回主頁" icon="pi pi-home" />
    </div>
    <Dashboard v-else />
  </div>
</template>

<script>
import Dashboard from '@/components/Dashboard.vue'
import { computed } from 'vue'
import { useStore } from 'vuex'

export default {
  name: 'AdminView',
  components: {
    Dashboard
  },
  setup() {
    const store = useStore()
    const hasHighestAuth = computed(() => store.getters['auth/hasHighestAuth'])
    
    return {
      hasHighestAuth
    }
  }
}
</script>

<style scoped>
.access-denied {
  text-align: center;
  padding: 3rem;
  margin: 2rem auto;
  max-width: 600px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.access-denied h1 {
  color: #ef4444;
  margin-bottom: 1rem;
}

.access-denied p {
  margin-bottom: 2rem;
  color: #4b5563;
}
</style>