import { defineConfig } from "vite";
import react      from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      // auth/login & auth/register
      "/auth": {
        target: "http://localhost:8080",
        changeOrigin: true
      },
      // anything under /tweet → /api/tweet
      "/tweet": {
        target: "http://localhost:8080/api/tweet",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/tweet/, "")
      }
    }
  }
});
