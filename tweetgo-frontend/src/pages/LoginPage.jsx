import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import API, { errorMessage } from "../api";
import { useAuth } from "../AuthContext";
import AuthLayout from "./AuthLayout";

export default function LoginPage() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const { login } = useAuth();
    const navigate = useNavigate();

    const submit = async (e) => {
        e.preventDefault();
        if (!username.trim() || !password) return setError("Please enter your username and password.");
        setLoading(true);
        setError("");
        try {
            const res = await API.post("/api/auth/login", { username: username.trim(), password });
            login(res.data.token);
            navigate("/");
        } catch (err) {
            setError(err.response?.status === 401 || err.response?.status === 400
                ? "Wrong username or password."
                : errorMessage(err));
        } finally {
            setLoading(false);
        }
    };

    return (
        <AuthLayout
            title="Welcome back"
            subtitle="Log in to see what people are talking about."
            footer={<>New here? <Link to="/register">Create an account</Link></>}
        >
            <form onSubmit={submit} className="form" noValidate>
                <label>
                    Username
                    <input value={username} onChange={(e) => setUsername(e.target.value)} autoComplete="username" autoFocus />
                </label>
                <label>
                    Password
                    <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} autoComplete="current-password" />
                </label>
                {error && <p className="form-error">{error}</p>}
                <button className="btn btn-primary btn-block" disabled={loading}>
                    {loading ? "Logging in…" : "Log in"}
                </button>
            </form>
        </AuthLayout>
    );
}
