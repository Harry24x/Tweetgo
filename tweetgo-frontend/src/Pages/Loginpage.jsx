import { useState } from "react";
import API from "../axios";

export default function LoginPage() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [msg, setMsg] = useState("");

    const handleLogin = async e => {
        e.preventDefault();
        try {
            const res = await API.post("/auth/login", { username, password });
            const token = res.data.token;
            localStorage.setItem("jwt", token);
            setMsg("✅ Logged IN");
        } catch (err) {
            setMsg("❌ Login failed: " + (err.response?.data || err.message));
        }
    };

    return (
        <div className="container d-flex align-items-center justify-content-center vh-100">
            <div className="card shadow-sm p-4" style={{ maxWidth: "420px", width: "100%" }}>
                {/* Logo & Header */}
                <div className="text-center mb-4">

                    <h4 className="fw-bold mb-1">TweetGo</h4>
                    <h6 className="text-secondary">OTH Regensburg</h6>
                    <p className="text-muted">@Harman Bhangu</p>
                </div>

                {/* Message */}
                {msg && (
                    <div className={`alert ${msg.startsWith("❌") ? "alert-danger" : "alert-success"} text-center`}>
                        {msg}
                    </div>
                )}

                {/* Login Form */}
                <form onSubmit={handleLogin} noValidate>
                    <div className="mb-3">
                        <label htmlFor="username" className="form-label">
                            Username
                        </label>
                        <input
                            id="username"
                            type="text"
                            className="form-control"
                            placeholder="Enter your username"
                            value={username}
                            onChange={e => setUsername(e.target.value)}
                            required
                        />
                    </div>

                    <div className="mb-4">
                        <label htmlFor="password" className="form-label">
                            Password
                        </label>
                        <input
                            id="password"
                            type="password"
                            className="form-control"
                            placeholder="Enter your password"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            required
                        />
                    </div>

                    <button type="submit" className="btn btn-success w-100">
                        Login
                    </button>

                    <div className="mt-3 text-center">
                        <small>
                            Don’t have an account?{" "}
                            <a href="/register" className="link-success">
                                Register
                            </a>
                        </small>
                    </div>
                </form>
            </div>
        </div>
    );
}
