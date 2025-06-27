import axios from "axios";

const API = axios.create({
    baseURL: "", // proxy will forward /auth/* and /tweet/* to localhost:8080
    headers: { "Content-Type": "application/json" },
});

API.interceptors.request.use(cfg => {
    const token = localStorage.getItem("jwt");
    if (token) cfg.headers.Authorization = `Bearer ${token}`;
    return cfg;
});

export default API;
