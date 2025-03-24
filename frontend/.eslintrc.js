module.exports = {
  root: true,
  env: {
    node: true,
    browser: true
  },
  extends: [
    'plugin:vue/vue3-essential',
    'eslint:recommended'
  ],
  parserOptions: {
    parser: '@babel/eslint-parser',
    ecmaVersion: 2020
  },
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    // 允許 PrimeVue 組件的單詞命名
    'vue/multi-word-component-names': ['error', {
      ignores: [
        'Button', 'Panel', 'Password', 'Calendar', 'Dropdown', 'Dialog', 
        'DataTable', 'Column', 'Textarea', 'Divider', 'Card', 'Menu',
        'Message', 'Toast', 'MultiSelect'
      ]
    }],
    // 禁用HTML保留名稱檢查
    'vue/no-reserved-component-names': 'off',
    // 允許空對象模式
    'no-empty-pattern': 'off',
    // 允許特定變量名不使用
    'no-unused-vars': ['error', { 
      'argsIgnorePattern': '^_',
      'varsIgnorePattern': '^_',
      'caughtErrorsIgnorePattern': '^_' 
    }]
  }
} 