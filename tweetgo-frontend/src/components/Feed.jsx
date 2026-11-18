import { useCallback, useEffect, useState } from "react";
import API, { errorMessage } from "../api";
import TweetCard from "./TweetCard";
import { Spinner } from "./ui";

const PAGE_SIZE = 20;

/**
 * Paginated list of posts loaded from `url` (?page=0,1,…).
 * `prepend` lets a parent push a freshly created post to the top.
 */
export default function Feed({ url, empty, prepend }) {
    const [tweets, setTweets] = useState([]);
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const load = useCallback(async (pageToLoad) => {
        setLoading(true);
        setError("");
        try {
            const res = await API.get(url, { params: { page: pageToLoad } });
            setTweets((prev) => (pageToLoad === 0 ? res.data : [...prev, ...res.data]));
            setHasMore(res.data.length === PAGE_SIZE);
            setPage(pageToLoad);
        } catch (err) {
            setError(errorMessage(err, "Could not load posts."));
        } finally {
            setLoading(false);
        }
    }, [url]);

    useEffect(() => {
        setTweets([]);
        load(0);
    }, [load]);

    useEffect(() => {
        if (prepend) setTweets((prev) => (prev.some((t) => t.id === prepend.id) ? prev : [prepend, ...prev]));
    }, [prepend]);

    const update = (t) => setTweets((prev) => prev.map((x) => (x.id === t.id ? t : x)));
    const remove = (id) => setTweets((prev) => prev.filter((x) => x.id !== id));

    return (
        <section className="card feed">
            {tweets.map((t) => (
                <TweetCard key={t.id} tweet={t} onChange={update} onDelete={remove} />
            ))}

            {loading && <Spinner />}
            {error && (
                <div className="notice">
                    <p className="form-error">{error}</p>
                    <button className="btn btn-ghost" onClick={() => load(page)}>Try again</button>
                </div>
            )}
            {!loading && !error && tweets.length === 0 && <div className="notice">{empty}</div>}
            {!loading && hasMore && (
                <button className="load-more" onClick={() => load(page + 1)}>Show more</button>
            )}
        </section>
    );
}
