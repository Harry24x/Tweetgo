import axios from "axios";

const API = axios.create({
    baseURL: "http://localhost:8080",
    headers: {
        "Content-Type": "application/json"
    }
});

API.interceptors.request.use((config) => {
    const token = localStorage.getItem("jwt");   // consistent with AuthContext
    if (token && !config.url.includes("/api/auth")) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default API;
