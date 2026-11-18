import { Link } from "react-router-dom";

/** "now", "5m", "3h", "2d", or a date like "12 Mar" / "12 Mar 2024". */
export function timeAgo(value) {
    const date = new Date(value);
    const seconds = Math.floor((Date.now() - date.getTime()) / 1000);
    if (seconds < 45) return "now";
    const minutes = Math.floor(seconds / 60);
    if (minutes < 60) return `${minutes}m`;
    const hours = Math.floor(minutes / 60);
    if (hours < 24) return `${hours}h`;
    const days = Math.floor(hours / 24);
    if (days < 7) return `${days}d`;
    const sameYear = date.getFullYear() === new Date().getFullYear();
    return date.toLocaleDateString(undefined, {
        day: "numeric",
        month: "short",
        ...(sameYear ? {} : { year: "numeric" }),
    });
}

export function fullDate(value) {
    return new Date(value).toLocaleString(undefined, {
        dateStyle: "medium",
        timeStyle: "short",
    });
}

/** Renders tweet text with clickable #hashtags. Links stop the card click. */
export function RichText({ text }) {
    const parts = text.split(/(#\w+)/g);
    return parts.map((part, i) =>
        /^#\w+$/.test(part) ? (
            <Link
                key={i}
                to={`/tag/${part.slice(1).toLowerCase()}`}
                className="hashtag"
                onClick={(e) => e.stopPropagation()}
            >
                {part}
            </Link>
        ) : (
            <span key={i}>{part}</span>
        )
    );
}
