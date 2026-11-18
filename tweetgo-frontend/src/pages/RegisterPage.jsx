import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import API, { errorMessage } from "../api";
import { useAuth } from "../AuthContext";
import AuthLayout from "./AuthLayout";

function validate({ username, password, confirm }) {
    if (username.trim().length < 3 || username.trim().length > 50) return "Username must be 3–50 characters.";
    if (!/^\w+$/.test(username.trim())) return "Username can only use letters, numbers and _.";
    if (password.length < 6) return "Password must be at least 6 characters.";
    if (password !== confirm) return "Passwords do not match.";
    return "";
}

export default function RegisterPage() {
    const [form, setForm] = useState({ username: "", password: "", confirm: "" });
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const { login } = useAuth();
    const navigate = useNavigate();

    const set = (key) => (e) => setForm({ ...form, [key]: e.target.value });

    const submit = async (e) => {
        e.preventDefault();
        const problem = validate(form);
        if (problem) return setError(problem);
        setLoading(true);
        setError("");
        const credentials = { username: form.username.trim(), password: form.password };
        try {
            await API.post("/api/auth/register", credentials);
            // log in straight away, no need to type everything again
            const res = await API.post("/api/auth/login", credentials);
            login(res.data.token);
            navigate("/");
        } catch (err) {
            setError(err.response?.status === 409 ? "This username is already taken." : errorMessage(err));
        } finally {
            setLoading(false);
        }
    };

    return (
        <AuthLayout
            title="Create your account"
            subtitle="Share short posts, follow people and join #topics."
            footer={<>Already have an account? <Link to="/login">Log in</Link></>}
        >
            <form onSubmit={submit} className="form" noValidate>
                <label>
                    Username
                    <input value={form.username} onChange={set("username")} autoComplete="username" autoFocus />
                </label>
                <label>
                    Password
                    <input type="password" value={form.password} onChange={set("password")} autoComplete="new-password" />
                    <small className="muted">At least 6 characters</small>
                </label>
                <label>
                    Confirm password
                    <input type="password" value={form.confirm} onChange={set("confirm")} autoComplete="new-password" />
                </label>
                {error && <p className="form-error">{error}</p>}
                <button className="btn btn-primary btn-block" disabled={loading}>
                    {loading ? "Creating account…" : "Create account"}
                </button>
            </form>
        </AuthLayout>
    );
}
