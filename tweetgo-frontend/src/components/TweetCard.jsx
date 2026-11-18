import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FiHeart, FiMessageCircle, FiTrash2 } from "react-icons/fi";
import API from "../api";
import { useAuth } from "../AuthContext";
import { fullDate, RichText, timeAgo } from "../lib/format";
import Avatar from "./Avatar";

/**
 * One post. Handles like/unlike and delete itself and reports changes
 * to the parent via onChange(updatedTweet) / onDelete(id).
 */
export default function TweetCard({ tweet, onChange, onDelete, linkToDetail = true, large = false }) {
    const { me } = useAuth();
    const navigate = useNavigate();
    const [busy, setBusy] = useState(false);
    const [confirmDelete, setConfirmDelete] = useState(false);
    const isMine = me?.username === tweet.authorUsername;

    const toggleLike = async (e) => {
        e.stopPropagation();
        if (busy) return;
        setBusy(true);
        const liked = tweet.likedByCurrentUser;
        // update the screen first, undo if the server says no
        onChange?.({ ...tweet, likedByCurrentUser: !liked, likesCount: tweet.likesCount + (liked ? -1 : 1) });
        try {
            if (liked) await API.delete(`/api/tweet/${tweet.id}/unlike`);
            else await API.post(`/api/tweet/${tweet.id}/like`);
        } catch {
            onChange?.(tweet);
        } finally {
            setBusy(false);
        }
    };

    const remove = async (e) => {
        e.stopPropagation();
        setBusy(true);
        try {
            await API.delete(`/api/tweet/${tweet.id}`);
            onDelete?.(tweet.id);
        } catch {
            setBusy(false);
            setConfirmDelete(false);
        }
    };

    const open = () => linkToDetail && navigate(`/tweet/${tweet.id}`);

    return (
        <article className={`tweet ${linkToDetail ? "clickable" : ""} ${large ? "large" : ""}`} onClick={open}>
            <Link to={`/u/${tweet.authorUsername}`} onClick={(e) => e.stopPropagation()}>
                <Avatar username={tweet.authorUsername} size={large ? 48 : 40} />
            </Link>

            <div className="tweet-body">
                <div className="tweet-meta">
                    <Link to={`/u/${tweet.authorUsername}`} className="username" onClick={(e) => e.stopPropagation()}>
                        @{tweet.authorUsername}
                    </Link>
                    {!large && (
                        <time className="muted" dateTime={tweet.timestamp} title={fullDate(tweet.timestamp)}>
                            · {timeAgo(tweet.timestamp)}
                        </time>
                    )}
                </div>

                <p className="tweet-text">
                    <RichText text={tweet.text} />
                </p>

                {large && <time className="muted small">{fullDate(tweet.timestamp)}</time>}

                <div className="tweet-actions">
                    <button
                        className={`action like ${tweet.likedByCurrentUser ? "active" : ""}`}
                        onClick={toggleLike}
                        aria-label={tweet.likedByCurrentUser ? "Unlike" : "Like"}
                    >
                        <FiHeart /> <span>{tweet.likesCount}</span>
                    </button>
                    <button className="action" onClick={(e) => { e.stopPropagation(); navigate(`/tweet/${tweet.id}`); }} aria-label="Comments">
                        <FiMessageCircle /> <span>{tweet.commentsCount}</span>
                    </button>

                    {isMine && onDelete && (
                        <div className="delete-area" onClick={(e) => e.stopPropagation()}>
                            {confirmDelete ? (
                                <>
                                    <span className="muted small">Delete?</span>
                                    <button className="btn btn-danger btn-sm" onClick={remove} disabled={busy}>Delete</button>
                                    <button className="btn btn-ghost btn-sm" onClick={() => setConfirmDelete(false)}>Cancel</button>
                                </>
                            ) : (
                                <button className="action danger" onClick={() => setConfirmDelete(true)} aria-label="Delete post">
                                    <FiTrash2 />
                                </button>
                            )}
                        </div>
                    )}
                </div>
            </div>
        </article>
    );
}
