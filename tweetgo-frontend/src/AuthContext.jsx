import { createContext, useCallback, useContext, useEffect, useState } from "react";
import API, { TOKEN_KEY } from "./api";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    // Read the token right away so a page refresh doesn't bounce you to login.
    const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY));
    const [me, setMe] = useState(null);

    const logout = useCallback(() => {
        localStorage.removeItem(TOKEN_KEY);
        setToken(null);
        setMe(null);
    }, []);

    const login = useCallback((newToken) => {
        localStorage.setItem(TOKEN_KEY, newToken);
        setToken(newToken);
    }, []);

    const refreshMe = useCallback(() => {
        return API.get("/api/users/me").then((res) => setMe(res.data));
    }, []);

    useEffect(() => {
        if (token) refreshMe().catch(() => {});
    }, [token, refreshMe]);

    useEffect(() => {
        window.addEventListener("auth:expired", logout);
        return () => window.removeEventListener("auth:expired", logout);
    }, [logout]);

    return (
        <AuthContext.Provider value={{ token, me, setMe, login, logout, refreshMe }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}
