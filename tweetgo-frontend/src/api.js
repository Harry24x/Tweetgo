import axios from "axios";

export const TOKEN_KEY = "jwt";

const API = axios.create({
    baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080",
    headers: { "Content-Type": "application/json" },
});

API.interceptors.request.use((config) => {
    const token = localStorage.getItem(TOKEN_KEY);
    if (token && !config.url.startsWith("/api/auth")) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// Expired or invalid session -> tell the app to log out.
API.interceptors.response.use(
    (res) => res,
    (err) => {
        if (err.response?.status === 401 && !err.config.url.startsWith("/api/auth")) {
            window.dispatchEvent(new Event("auth:expired"));
        }
        return Promise.reject(err);
    }
);

/** Turns any API error into a short readable message. */
export function errorMessage(err, fallback = "Something went wrong. Please try again.") {
    const data = err?.response?.data;
    if (typeof data === "string" && data.trim()) return data;
    if (data?.message) return data.message;
    if (err?.code === "ERR_NETWORK") return "Can't reach the server. Is the backend running?";
    return fallback;
}

export default API;
