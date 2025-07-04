import React, { useEffect, useState, useContext } from "react";
import API from "../axios";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../Context/AuthContext";
import { Link } from "react-router-dom";


export default function Homepage() {
    const { logout , token } = useContext(AuthContext);
    const navigate = useNavigate();

    const [feed, setFeed] = useState(null);
    const [error, setError] = useState("");
    const [newText, setNewText] = useState("");
    const [posting, setPosting] = useState(false);

    useEffect(() => {
        if (!token) return setFeed([]);

        setFeed(null);
        API.get("/api/tweet/feed")   // <-- this must match your controller path!
            .then(res => setFeed(res.data))
            .catch(err => {
                console.error(err);
                setError("Could not load feed.");
                setFeed([]);
            });
    }, [token]);


    // Logout
    const handleLogout = () => {
        logout();           // wipe token from context / localStorage
        navigate("/login"); // step-6: redirect back to login
    };

    const handlePost = async (e) => {
        e.preventDefault();
        if (!newText.trim()) return;
        setPosting(true);
        try {
            console.log("Posting:", newText);
            const resp = await API.post("/api/tweet", { text: newText });
            console.log("Got response:", resp);
            setFeed([resp.data, ...feed]);
            setNewText("");
        } catch (err) {
            console.error("POST tweet failed:", err.response || err);
            setError("Could not post tweet.");
        } finally {
            setPosting(false);
        }
    };


    return (
        <div className="d-flex flex-column vh-100">
            {/* Navbar */}
            <nav className="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
                <div className="container">

                    <Link className="navbar-brand d-flex align-items-center" to="/feed">
                        <img
                            src="/logo192.png"
                            alt="TweetGo"
                            width="32"
                            height="32"
                            className="me-2 rounded-circle"
                        />
                        <span className="fs-4">TweetGo</span>
                    </Link>
                    <div className="collapse navbar-collapse">
                        <ul className="navbar-nav me-auto">
                            <li className="nav-item">
                                <Link to="/discover" className="nav-link">Discover</Link>
                            </li>
                            <li className="nav-item">
                                <Link to="/profile" className="nav-link">My Profile</Link>
                            </li>
                        </ul>
                    </div>

                    <button
                        className="btn btn-outline-light"
                        onClick={handleLogout}
                    >
                        Logout
                    </button>
                </div>
            </nav>

            {/* Main Content */}
            <main className="container flex-grow-1 my-4">
                {/* New Tweet Card */}
                <div className="card mb-4 shadow-sm">
                    <div className="card-body">
                        <form onSubmit={handlePost}>
                        <div className="d-flex align-items-start">
                                <img
                                    src="/avatar-placeholder.png"
                                    alt="Your avatar"
                                    width="48"
                                    height="48"
                                    className="rounded-circle me-3"
                                />
                                <textarea
                                    className="form-control"
                                    rows="3"
                                    placeholder="What’s happening?"
                                    value={newText}
                                    onChange={(e) => setNewText(e.target.value)}
                                    disabled={posting}
                                />
                            </div>
                            <div className="mt-3 text-end">
                                <button
                                    className="btn btn-primary"
                                    type="submit"
                                    disabled={posting}
                                >
                                    {posting ? (
                                        <>
                      <span
                          className="spinner-border spinner-border-sm me-2"
                          role="status"
                          aria-hidden="true"
                      ></span>
                                            Posting…
                                        </>
                                    ) : (
                                        "Tweet"
                                    )}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

                {/* Error */}
                {error && (
                    <div className="alert alert-danger text-center">{error}</div>
                )}

                {/* Loading */}
                {feed === null && (
                    <div className="text-center py-5">
                        <div
                            className="spinner-border text-primary"
                            role="status"
                        ></div>
                    </div>
                )}

                {/* Empty */}
                {Array.isArray(feed) && feed.length === 0 && !error && (
                    <p className="text-center text-muted fs-5">
                        Nothing to show
                    </p>
                )}

                {/* Tweet Feed */}
                {Array.isArray(feed) &&
                    feed.map((tweet) => (
                        <div className="card mb-3 shadow-sm" key={tweet.id}>
                            <div className="card-header bg-white border-bottom-0 d-flex">
                                <img
                                    src="/avatar-placeholder.png"
                                    alt={`${tweet.authorUsername}’s avatar`}
                                    width="48"
                                    height="48"
                                    className="rounded-circle me-3"
                                />
                                <div className="flex-grow-1">
                                    <div className="d-flex justify-content-between">
                                        <h6 className="mb-0">{tweet.authorUsername}</h6>
                                        <small className="text-muted">
                                            {new Date(tweet.timestamp).toLocaleString()}
                                        </small>
                                    </div>
                                    <p className="card-text mt-2">{tweet.text}</p>
                                </div>
                            </div>
                            <div className="card-footer bg-white border-top-0 d-flex justify-content-end">
                                <button
                                    className={`btn btn-sm me-2 ${
                                        tweet.likedByCurrentUser
                                            ? "btn-danger"
                                            : "btn-outline-primary"
                                    }`}
                                    onClick={async () => {
                                        try {
                                            await API({
                                                method: tweet.likedByCurrentUser
                                                    ? "delete"
                                                    : "post",
                                                url: `/api/tweet/${tweet.id}/${
                                                    tweet.likedByCurrentUser ? "unlike" : "like"
                                                }`,
                                            });
                                            tweet.likedByCurrentUser = !tweet.likedByCurrentUser;
                                            tweet.likesCount += tweet.likedByCurrentUser ? 1 : -1;
                                            setFeed([...feed]);
                                        } catch (e) {
                                            console.error(e);
                                        }
                                    }}
                                >
                                    {tweet.likedByCurrentUser ? "💔 Unlike" : "❤️ Like"}{" "}
                                    <span className="badge bg-secondary">
                    {tweet.likesCount}
                  </span>
                                </button>
                            </div>
                        </div>
                    ))}
            </main>

            {/* Footer */}
            <footer className="bg-light py-3 mt-auto">
                <div className="container text-center text-muted small">
                    © 2025 TweetGo • OTH Regensburg • @Harman Bhangu
                </div>
            </footer>
        </div>
    );
}