import { useState } from "react";
import API from "../axios";
import { useNavigate } from "react-router-dom";

export default function Registerpage() {
    const [form, setForm] = useState({ username: "", password: "", confirm: "" });
    const [msg, setMsg] = useState("");
    const nav = useNavigate();

    const submit = async e => {
        e.preventDefault();
        if (form.password !== form.confirm) {
            return setMsg("❌ Passwords do not match");
        }
        try {
            await API.post("/api/auth/register", {
                username: form.username,
                password: form.password
            });
            setMsg("✅ Registered! Redirecting to login…");
            setTimeout(() => nav("/login"), 1500);
        } catch (err) {
            setMsg(err.response?.data || err.message);
        }
    };

    return (
        <div className="container d-flex align-items-center justify-content-center vh-100">
            <div className="card shadow-lg p-4" style={{ maxWidth: '420px', width: '100%' }}>
                <div className="text-center mb-4">
                    <h4 className="fw-bold mb-1">TweetGo</h4>
                    <h6 className="text-secondary">OTH Regensburg</h6>
                    <p className="text-muted">@Harman Bhangu</p>
                </div>

                {msg && (
                    <div className="alert alert-info text-center mb-3">
                        {msg}
                    </div>
                )}

                <form onSubmit={submit} noValidate>
                    <div className="mb-3">
                        <label htmlFor="username" className="form-label">
                            Username
                        </label>
                        <input
                            id="username"
                            type="text"
                            className="form-control"
                            value={form.username}
                            onChange={e => setForm({ ...form, username: e.target.value })}
                            required
                        />
                    </div>

                    <div className="mb-3">
                        <label htmlFor="password" className="form-label">
                            Password
                        </label>
                        <input
                            id="password"
                            type="password"
                            className="form-control"
                            value={form.password}
                            onChange={e => setForm({ ...form, password: e.target.value })}
                            required
                        />
                    </div>

                    <div className="mb-4">
                        <label htmlFor="confirm" className="form-label">
                            Confirm Password
                        </label>
                        <input
                            id="confirm"
                            type="password"
                            className="form-control"
                            value={form.confirm}
                            onChange={e => setForm({ ...form, confirm: e.target.value })}
                            required
                        />
                    </div>

                    <button type="submit" className="btn btn-primary w-100 mb-2">
                        Register
                    </button>

                    <div className="text-center">
                        <small>
                            Already have an account?{" "}
                            <a href="/login" className="link-primary">
                                Login
                            </a>
                        </small>
                    </div>
                </form>
            </div>
        </div>
    );
}
