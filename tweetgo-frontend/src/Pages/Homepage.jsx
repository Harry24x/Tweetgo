import React, { useEffect, useState, useContext } from "react";
import API from "../axios";
import { AuthContext } from "../Context/AuthContext";

export default function Homepage() {
    const { setToken } = useContext(AuthContext);
    const [feed, setFeed] = useState(null);     // null = loading, [] = empty
    const [error, setError] = useState("");
    const [newText, setNewText] = useState("");
    const [posting, setPosting] = useState(false);

    // Load feed on mount
    useEffect(() => {
        API.get("/tweet/feed")
            .then(res => setFeed(res.data))
            .catch(err => {
                console.error(err);
                setError("Could not load feed.");
                setFeed([]);
            });
    }, []);

    // Logout
    const handleLogout = () => {
        localStorage.removeItem("jwt");
        setToken(null);
        window.location.href = "/login";
    };

    // Post a new tweet
    const handlePost = async e => {
        e.preventDefault();
        if (!newText.trim()) return;
        setPosting(true);
        try {
            const resp = await API.post("/tweet", { text: newText });
            setFeed([resp.data, ...feed]);
            setNewText("");
        } catch (err) {
            console.error(err);
        } finally {
            setPosting(false);
        }
    };

    return (
        <div>
            {/* Navbar */}
            <nav className="navbar navbar-expand-lg navbar-dark bg-primary">
                <div className="container">
                    <a className="navbar-brand" href="/">
                        <img
                            src="/logo.png"
                            alt="TweetGo Logo"
                            width="30"
                            height="30"
                            className="d-inline-block align-text-top me-2"
                        />
                        TweetGo
                    </a>
                    <button
                        className="btn btn-outline-light"
                        onClick={handleLogout}
                    >
                        Logout
                    </button>
                </div>
            </nav>

            <div className="container my-4">
                {/* New Tweet */}
                <div className="card mb-4 shadow-sm">
                    <div className="card-body">
                        <form onSubmit={handlePost} className="d-flex">
                            <input
                                type="text"
                                className="form-control me-2"
                                placeholder="What's happening?"
                                value={newText}
                                onChange={e => setNewText(e.target.value)}
                                disabled={posting}
                                required
                            />
                            <button
                                className="btn btn-primary"
                                type="submit"
                                disabled={posting}
                            >
                                {posting ? "Posting…" : "Tweet"}
                            </button>
                        </form>
                    </div>
                </div>

                {/* Error */}
                {error && (
                    <div className="alert alert-danger text-center">
                        {error}
                    </div>
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
                    <p className="text-center text-muted">
                        No tweets to display.
                    </p>
                )}

                {/* Feed */}
                {Array.isArray(feed) &&
                    feed.map(tweet => (
                        <div className="card mb-3" key={tweet.id}>
                            <div className="card-header d-flex justify-content-between align-items-center">
                <span className="fw-bold">
                  {tweet.authorUsername}
                </span>
                                <small className="text-muted">
                                    {new Date(tweet.timestamp).toLocaleString()}
                                </small>
                            </div>
                            <div className="card-body">
                                <p className="card-text">{tweet.text}</p>
                                <button
                                    className={
                                        "btn btn-sm " +
                                        (tweet.likedByCurrentUser
                                            ? "btn-danger"
                                            : "btn-outline-primary")
                                    }
                                    onClick={async () => {
                                        try {
                                            await API({
                                                method: tweet.likedByCurrentUser
                                                    ? "delete"
                                                    : "post",
                                                url: `/tweet/${tweet.id}/${
                                                    tweet.likedByCurrentUser ? "unlike" : "like"
                                                }`,
                                            });
                                            tweet.likedByCurrentUser = !tweet.likedByCurrentUser;
                                            tweet.likesCount += tweet.likedByCurrentUser
                                                ? 1
                                                : -1;
                                            setFeed([...feed]);
                                        } catch (e) {
                                            console.error(e);
                                        }
                                    }}
                                >
                                    {tweet.likedByCurrentUser
                                        ? "💔 Unlike"
                                        : "❤️ Like"}{" "}
                                    ({tweet.likesCount})
                                </button>
                            </div>
                        </div>
                    ))}
            </div>

            {/* Footer */}
            <footer className="bg-light py-3 mt-auto">
                <div className="container text-center text-muted">
                    OTH Regensburg • @Harman Bhangu
                </div>
            </footer>
        </div>
    );
}
