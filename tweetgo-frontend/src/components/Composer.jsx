import { useState } from "react";
import API, { errorMessage } from "../api";
import { useAuth } from "../AuthContext";
import Avatar from "./Avatar";

const MAX = 280;

export default function Composer({ onPosted }) {
    const { me } = useAuth();
    const [text, setText] = useState("");
    const [posting, setPosting] = useState(false);
    const [error, setError] = useState("");

    const left = MAX - text.length;
    const canPost = text.trim().length > 0 && left >= 0 && !posting;

    const submit = async (e) => {
        e?.preventDefault();
        if (!canPost) return;
        setPosting(true);
        setError("");
        try {
            const res = await API.post("/api/tweet", { text: text.trim() });
            setText("");
            onPosted?.(res.data);
        } catch (err) {
            setError(errorMessage(err, "Could not post. Please try again."));
        } finally {
            setPosting(false);
        }
    };

    return (
        <form className="composer card" onSubmit={submit}>
            <Avatar username={me?.username} />
            <div className="composer-body">
                <textarea
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                    onKeyDown={(e) => {
                        if (e.key === "Enter" && (e.ctrlKey || e.metaKey)) submit();
                    }}
                    placeholder="What's happening? Use #hashtags to join topics."
                    rows={3}
                    aria-label="Write a post"
                />
                {error && <p className="form-error">{error}</p>}
                <div className="composer-footer">
                    <span className={`counter ${left < 0 ? "over" : left <= 20 ? "warn" : ""}`}>
                        {text.length > 0 && left}
                    </span>
                    <button className="btn btn-primary" disabled={!canPost}>
                        {posting ? "Posting…" : "Post"}
                    </button>
                </div>
            </div>
        </form>
    );
}
