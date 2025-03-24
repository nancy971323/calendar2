<template>
  <span :class="['security-level-badge', getSecurityLevelClass()]">
    {{ getSecurityLevelName() }}
  </span>
</template>

<script>
export default {
  name: 'SecurityLevelBadge',
  props: {
    level: String
  },
  setup(props) {
    const getSecurityLevelClass = () => {
      switch(props.level) {
        case 'LEVEL_1': return 'security-level-1'
        case 'LEVEL_2': return 'security-level-2'
        case 'LEVEL_3': return 'security-level-3'
        case 'LEVEL_4': return 'security-level-4'
        default: return ''
      }
    }
    
    const getSecurityLevelName = () => {
      switch(props.level) {
        case 'LEVEL_1': return '最高權限'
        case 'LEVEL_2': return '高級權限'
        case 'LEVEL_3': return '中級權限'
        case 'LEVEL_4': return '基本權限'
        default: return props.level
      }
    }
    
    return {
      getSecurityLevelClass,
      getSecurityLevelName
    }
  }
}
</script>

<style scoped>
.security-level-badge {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-weight: bold;
}

.security-level-1 {
  background-color: #ffcdd2;
  color: #c62828;
}

.security-level-2 {
  background-color: #fff9c4;
  color: #f57f17;
}

.security-level-3 {
  background-color: #b3e5fc;
  color: #0277bd;
}

.security-level-4 {
  background-color: #c8e6c9;
  color: #2e7d32;
}
</style> 