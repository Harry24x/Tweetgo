// src/App.jsx
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Loginpage from "./Pages/Loginpage";
import Registerpage from "./Pages/Registerpage";
import Homepage from "./Pages/Homepage";
import PrivateRoute from "./components/PrivateRoute.jsx";
import AuthProvider from "./Context/AuthContext.jsx";

export default function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    {/* Public routes */}
                    <Route path="/" element={<Loginpage />} />
                    <Route path="/login" element={<Loginpage />} />
                    <Route path="/register" element={<Registerpage />} />

                    {/* Protected routes */}
                    <Route element={<PrivateRoute />}>
                        <Route path="/feed" element={<Homepage />} />
                        {/* add more protected pages here */}
                    </Route>

                    {/* Fallback */}
                    <Route path="*" element={<Loginpage />} />
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}

