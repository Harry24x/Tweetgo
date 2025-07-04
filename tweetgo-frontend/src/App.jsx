// src/App.jsx
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Loginpage from "./Pages/Loginpage";
import Registerpage from "./Pages/Registerpage";
import Homepage from "./Pages/Homepage";
import DiscoverPage   from "./Pages/DiscoverPage";
import PrivateRoute from "./components/PrivateRoute.jsx";
import AuthProvider from "./Context/AuthContext.jsx";
import ProfilePage from "./Pages/ProfilePage.jsx";

export default function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>

                    <Route path="/" element={<Loginpage />} />
                    <Route path="/login" element={<Loginpage />} />
                    <Route path="/register" element={<Registerpage />} />
                    <Route path="discover" element={<DiscoverPage/>} />
                    <Route path="profile" element={<ProfilePage/>} />



                    <Route path="feed"     element={<Homepage />} />
                    <Route path="discover" element={<DiscoverPage />} />

                    {/* Fallback */}
                    <Route path="*" element={<Navigate to="/login" replace />} />
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}

