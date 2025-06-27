import React, { createContext, useState, useEffect } from "react";

// Create AuthContext to hold JWT token and updater
export const AuthContext = createContext({
    token: null,
    setToken: () => {}
});

// Provider component to wrap the app
export function AuthProvider({ children }) {
    // Initialize token from localStorage if present
    const [token, setToken] = useState(() => {
        return localStorage.getItem("jwt") || null;
    });

    // Side effect: whenever token changes, sync to localStorage
    useEffect(() => {
        if (token) {
            localStorage.setItem("jwt", token);
        } else {
            localStorage.removeItem("jwt");
        }
    }, [token]);

    return (
        <AuthContext.Provider value={{ token, setToken }}>
            {children}
        </AuthContext.Provider>
    );
}
