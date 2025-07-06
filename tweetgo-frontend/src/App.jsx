
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Loginpage from "./Pages/Loginpage";
import Registerpage from "./Pages/Registerpage";
import Homepage from "./Pages/Homepage";
import DiscoverPage   from "./Pages/DiscoverPage";
import AuthProvider from "./Context/AuthContext.jsx";
import ProfilePage from "./Pages/ProfilePage";
import TweetDetailPage from "./Pages/TweetDetailPage";
import "./App.css";

export default function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>

                    <Route path="/" element={<Loginpage />} />
                    <Route path="/login" element={<Loginpage />} />
                    <Route path="/register" element={<Registerpage />} />

                    <Route path="/discover" element={<DiscoverPage/>} />

                    <Route path="/feed" element={<Homepage />} />

                    <Route path="/profile" element={<ProfilePage />} />

                    <Route path="/tweet/:id" element={<TweetDetailPage />} />

                    <Route path="*" element={<Navigate to="/login" replace />} />
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}

