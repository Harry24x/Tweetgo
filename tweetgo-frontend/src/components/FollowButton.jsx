import { useState } from "react";
import API from "../api";

/** Follow / Unfollow toggle. Calls onChange(updatedUser) after success. */
export default function FollowButton({ user, onChange, size }) {
    const [busy, setBusy] = useState(false);
    const [hover, setHover] = useState(false);
    const following = user.followedByCurrentUser;

    const toggle = async (e) => {
        e.preventDefault();
        e.stopPropagation();
        setBusy(true);
        try {
            await API.post(`/api/users/${user.username}/${following ? "unfollow" : "follow"}`);
            onChange?.({
                ...user,
                followedByCurrentUser: !following,
                followersCount: user.followersCount + (following ? -1 : 1),
            });
        } finally {
            setBusy(false);
        }
    };

    return (
        <button
            className={`btn ${following ? "btn-outline" : "btn-dark"} ${size === "sm" ? "btn-sm" : ""} follow-btn`}
            onClick={toggle}
            disabled={busy}
            onMouseEnter={() => setHover(true)}
            onMouseLeave={() => setHover(false)}
        >
            {following ? (hover ? "Unfollow" : "Following") : "Follow"}
        </button>
    );
}
