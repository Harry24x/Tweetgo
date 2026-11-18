import { Link } from "react-router-dom";

export default function NotFoundPage() {
    return (
        <div className="card notice">
            <h1>Page not found</h1>
            <p className="muted">This page doesn't exist.</p>
            <Link to="/" className="btn btn-primary">Go home</Link>
        </div>
    );
}
