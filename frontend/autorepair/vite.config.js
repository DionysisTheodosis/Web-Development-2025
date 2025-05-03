import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  css: {
    postcss:  './postcss.config.mjs',
  },
  server: {
    host: '0.0.0.0', // Add this line
    port: 3000,       // Keep existing port
    strictPort: true  // Optional: enforce port
  }
})