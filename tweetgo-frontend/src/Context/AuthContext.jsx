import React, { createContext, useState, useEffect } from "react";

export const AuthContext = createContext(null);

export default function AuthProvider({ children }) {
    const [token, setToken] = useState(null);

    useEffect(() => {
        setToken(localStorage.getItem("jwt"));
    }, []);

    const login = (t) => {
        localStorage.setItem("jwt", t);
        setToken(t);
    };
    const logout = () => {
        localStorage.removeItem("jwt");
        setToken(null);
    };

    return (
        <AuthContext.Provider value={{ token, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}
