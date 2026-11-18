import { BrowserRouter, Navigate, Outlet, Route, Routes } from "react-router-dom";
import { AuthProvider, useAuth } from "./AuthContext";
import Layout from "./components/Layout";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import HomePage from "./pages/HomePage";
import ExplorePage from "./pages/ExplorePage";
import SearchPage from "./pages/SearchPage";
import ProfilePage from "./pages/ProfilePage";
import TweetPage from "./pages/TweetPage";
import HashtagPage from "./pages/HashtagPage";
import NotFoundPage from "./pages/NotFoundPage";

function RequireAuth() {
    const { token } = useAuth();
    return token ? <Outlet /> : <Navigate to="/login" replace />;
}

function GuestOnly() {
    const { token } = useAuth();
    return token ? <Navigate to="/" replace /> : <Outlet />;
}

function MyProfileRedirect() {
    const { me } = useAuth();
    return me ? <Navigate to={`/u/${me.username}`} replace /> : null;
}

export default function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    <Route element={<GuestOnly />}>
                        <Route path="/login" element={<LoginPage />} />
                        <Route path="/register" element={<RegisterPage />} />
                    </Route>

                    <Route element={<RequireAuth />}>
                        <Route element={<Layout />}>
                            <Route path="/" element={<HomePage />} />
                            <Route path="/explore" element={<ExplorePage />} />
                            <Route path="/search" element={<SearchPage />} />
                            <Route path="/profile" element={<MyProfileRedirect />} />
                            <Route path="/u/:username" element={<ProfilePage />} />
                            <Route path="/tweet/:id" element={<TweetPage />} />
                            <Route path="/tag/:name" element={<HashtagPage />} />
                            <Route path="*" element={<NotFoundPage />} />
                        </Route>
                    </Route>
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}
