const COLORS = ["#2563eb", "#0891b2", "#059669", "#d97706", "#dc2626", "#7c3aed", "#db2777", "#4b5563"];

function colorFor(name = "") {
    let hash = 0;
    for (const ch of name) hash = (hash * 31 + ch.charCodeAt(0)) >>> 0;
    return COLORS[hash % COLORS.length];
}

/** Round avatar with the user's first letter (no image upload needed). */
export default function Avatar({ username = "?", size = 40 }) {
    return (
        <span
            className="avatar"
            style={{ width: size, height: size, fontSize: size * 0.42, background: colorFor(username) }}
            aria-hidden="true"
        >
            {username.charAt(0).toUpperCase()}
        </span>
    );
}
