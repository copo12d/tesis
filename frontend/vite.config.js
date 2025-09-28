import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tsconfigPaths from "vite-tsconfig-paths"

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tsconfigPaths()],
  server: {
    host: true,
    watch: {
      usePolling: true
    },
    port: 5173,
    strictPort: true,
    proxy: {
      '/api': {
        target: 'http://backend:8080',
        changeOrigin: true,
        secure: false
      }
    }
  }
})
