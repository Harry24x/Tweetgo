import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import API, { errorMessage } from "../api";
import Avatar from "../components/Avatar";
import FollowButton from "../components/FollowButton";
import { PageHeader, Spinner } from "../components/ui";
import { FiSearch } from "react-icons/fi";

export default function SearchPage() {
    const [query, setQuery] = useState("");
    const [users, setUsers] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    // search 300 ms after the user stops typing
    useEffect(() => {
        const q = query.trim();
        if (!q) {
            setUsers(null);
            return;
        }
        const timer = setTimeout(async () => {
            setLoading(true);
            setError("");
            try {
                const res = await API.get("/api/users/search", { params: { q } });
                setUsers(res.data);
            } catch (err) {
                setError(errorMessage(err));
            } finally {
                setLoading(false);
            }
        }, 300);
        return () => clearTimeout(timer);
    }, [query]);

    const update = (u) => setUsers((prev) => prev.map((x) => (x.username === u.username ? u : x)));

    return (
        <>
            <PageHeader title="Search" subtitle="Find people by username" />
            <div className="search-box card">
                <FiSearch className="muted" />
                <input
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                    placeholder="Search users…"
                    autoFocus
                    aria-label="Search users"
                />
            </div>

            <section className="card">
                {loading && <Spinner />}
                {error && <div className="notice form-error">{error}</div>}
                {!loading && users === null && <div className="notice muted">Start typing to find people.</div>}
                {!loading && users?.length === 0 && <div className="notice muted">No users found for "{query.trim()}".</div>}
                {!loading && users?.map((u) => (
                    <Link to={`/u/${u.username}`} key={u.username} className="user-row">
                        <Avatar username={u.username} size={44} />
                        <div className="user-row-info">
                            <strong>{u.fullName || u.username}</strong>
                            <span className="muted">@{u.username}</span>
                            <span className="muted small">
                                {u.followersCount} followers · {u.followingCount} following
                            </span>
                        </div>
                        <FollowButton user={u} onChange={update} size="sm" />
                    </Link>
                ))}
            </section>
        </>
    );
}
