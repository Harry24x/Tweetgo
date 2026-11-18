import { Link } from "react-router-dom";

export default function AuthLayout({ title, subtitle, children, footer }) {
    return (
        <div className="auth-page">
            <div className="auth-card card">
                <Link to="/login" className="brand brand-lg">
                    <img src="/favicon.svg" alt="" width="34" height="34" />
                    TweetGo
                </Link>
                <h1>{title}</h1>
                <p className="muted">{subtitle}</p>
                {children}
                <p className="auth-footer muted">{footer}</p>
            </div>
        </div>
    );
}
