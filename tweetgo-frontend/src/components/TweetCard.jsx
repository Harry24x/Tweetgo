import API from "../axios";
import { useState } from "react";

export default function TweetCard({ tweet }) {
    const [liked, setLiked] = useState(tweet.likedByCurrentUser);
    const [count, setCount] = useState(tweet.likesCount);

    const toggle = () => {
        const call = liked
            ? API.delete(`/api/tweet/${tweet.id}/unlike`)
            : API.post(`/api/tweet/${tweet.id}/like`);
        call.then(() => {
            setLiked(!liked);
            setCount(count + (liked ? -1 : 1));
        });
    };

    return (
        <div className="border p-2 mb-2">
            <strong>{tweet.authorUsername}</strong>
            <p>{tweet.text}</p>
            <button onClick={toggle}>
                {liked ? "💔 Unlike" : "❤️ Like"} ({count})
            </button>
        </div>
    );
}
