// tweetgo-frontend/src/axios.js
import axios from "axios";

const API = axios.create({
    baseURL: "http://localhost:8080",            // All calls go to http://localhost:8080/api/...
    headers: {
        "Content-Type": "application/json"
    }
});

// Interceptor: runs before every request
API.interceptors.request.use((config) => {
    const token = localStorage.getItem("jwt");   // your login stored this
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default API;
