const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8081,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  css: {
    loaderOptions: {
      scss: {
        additionalData: `
          $primary-color: #3B82F6;
          $secondary-color: #64748b;
          $success-color: #10b981;
          $info-color: #0ea5e9;
          $warning-color: #f59e0b;
          $danger-color: #ef4444;
        `
      }
    }
  }
})