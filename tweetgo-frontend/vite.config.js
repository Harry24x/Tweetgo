import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,           // your dev port
    proxy: {
      // Proxy /auth/* and /tweet/* to your backend on 8080
      "/auth": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      "/tweet": {
        target: "http://localhost:8080",
        changeOrigin: true,
      }
    }
  }
});
