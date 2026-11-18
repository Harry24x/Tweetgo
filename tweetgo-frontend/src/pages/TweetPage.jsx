import { useCallback, useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { FiArrowLeft } from "react-icons/fi";
import API, { errorMessage } from "../api";
import { useAuth } from "../AuthContext";
import Avatar from "../components/Avatar";
import TweetCard from "../components/TweetCard";
import { Spinner } from "../components/ui";
import { fullDate, timeAgo } from "../lib/format";

const PAGE_SIZE = 20;

export default function TweetPage() {
    const { id } = useParams();
    const { me } = useAuth();
    const navigate = useNavigate();
    const [tweet, setTweet] = useState(null);
    const [error, setError] = useState("");
    const [comments, setComments] = useState([]);
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(false);
    const [loadingComments, setLoadingComments] = useState(true);
    const [text, setText] = useState("");
    const [posting, setPosting] = useState(false);
    const [postError, setPostError] = useState("");

    useEffect(() => {
        setTweet(null);
        setError("");
        API.get(`/api/tweet/${id}`)
            .then((res) => setTweet(res.data))
            .catch((err) => setError(err.response?.status === 404 ? "This post doesn't exist or was deleted." : errorMessage(err)));
    }, [id]);

    const loadComments = useCallback(async (p) => {
        setLoadingComments(true);
        try {
            const res = await API.get(`/api/tweet/${id}/comments`, { params: { page: p } });
            setComments((prev) => (p === 0 ? res.data : [...prev, ...res.data]));
            setHasMore(res.data.length === PAGE_SIZE);
            setPage(p);
        } catch {
            /* comments are optional, the post still shows */
        } finally {
            setLoadingComments(false);
        }
    }, [id]);

    useEffect(() => {
        loadComments(0);
    }, [loadComments]);

    const addComment = async (e) => {
        e.preventDefault();
        if (!text.trim()) return;
        setPosting(true);
        setPostError("");
        try {
            const res = await API.post(`/api/tweet/${id}/comments`, { text: text.trim() });
            setComments((prev) => [res.data, ...prev]);
            setTweet((t) => ({ ...t, commentsCount: t.commentsCount + 1 }));
            setText("");
        } catch (err) {
            setPostError(errorMessage(err, "Could not add your reply."));
        } finally {
            setPosting(false);
        }
    };

    const back = (
        <button className="back-link" onClick={() => (window.history.length > 1 ? navigate(-1) : navigate("/"))}>
            <FiArrowLeft /> Back
        </button>
    );

    if (error) return <>{back}<div className="card notice"><p>{error}</p></div></>;
    if (!tweet) return <>{back}<div className="card"><Spinner /></div></>;

    return (
        <>
            {back}
            <section className="card">
                <TweetCard
                    tweet={tweet}
                    large
                    linkToDetail={false}
                    onChange={setTweet}
                    onDelete={() => navigate("/", { replace: true })}
                />

                <form className="reply-form" onSubmit={addComment}>
                    <Avatar username={me?.username} size={34} />
                    <input
                        value={text}
                        onChange={(e) => setText(e.target.value)}
                        placeholder="Write a reply…"
                        maxLength={500}
                        aria-label="Write a reply"
                    />
                    <button className="btn btn-primary btn-sm" disabled={!text.trim() || posting}>
                        {posting ? "…" : "Reply"}
                    </button>
                </form>
                {postError && <p className="form-error pad">{postError}</p>}

                {comments.map((c) => (
                    <div className="comment" key={c.id}>
                        <Link to={`/u/${c.authorUsername}`}><Avatar username={c.authorUsername} size={34} /></Link>
                        <div>
                            <div className="tweet-meta">
                                <Link to={`/u/${c.authorUsername}`} className="username">@{c.authorUsername}</Link>
                                <time className="muted" title={fullDate(c.timestamp)}>· {timeAgo(c.timestamp)}</time>
                            </div>
                            <p className="tweet-text">{c.text}</p>
                        </div>
                    </div>
                ))}
                {loadingComments && <Spinner />}
                {!loadingComments && comments.length === 0 && <div className="notice muted">No replies yet.</div>}
                {!loadingComments && hasMore && (
                    <button className="load-more" onClick={() => loadComments(page + 1)}>Show more replies</button>
                )}
            </section>
        </>
    );
}
