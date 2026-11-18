import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { FiCompass, FiHome, FiLogOut, FiSearch, FiUser } from "react-icons/fi";
import { useAuth } from "../AuthContext";
import Avatar from "./Avatar";

export default function Layout() {
    const { me, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    const links = [
        { to: "/", label: "Home", icon: FiHome, end: true },
        { to: "/explore", label: "Explore", icon: FiCompass },
        { to: "/search", label: "Search", icon: FiSearch },
        { to: me ? `/u/${me.username}` : "/profile", label: "Profile", icon: FiUser },
    ];

    return (
        <div className="app">
            <header className="topbar">
                <div className="topbar-inner">
                    <NavLink to="/" className="brand">
                        <img src="/favicon.svg" alt="" width="26" height="26" />
                        TweetGo
                    </NavLink>

                    <nav className="nav">
                        {links.map((link) => {
                            const Icon = link.icon;
                            return (
                                <NavLink key={link.label} to={link.to} end={link.end} className="nav-link" title={link.label}>
                                    <Icon size={19} />
                                    <span>{link.label}</span>
                                </NavLink>
                            );
                        })}
                    </nav>

                    <div className="topbar-user">
                        {me && <Avatar username={me.username} size={30} />}
                        <button className="icon-btn" onClick={handleLogout} title="Log out" aria-label="Log out">
                            <FiLogOut size={18} />
                        </button>
                    </div>
                </div>
            </header>

            <main className="main">
                <Outlet />
            </main>

            <footer className="footer">
                TweetGo · built by Harman Bhangu ·{" "}
                <a href="https://github.com/Harry24x/Tweetgo" target="_blank" rel="noreferrer">
                    Source on GitHub
                </a>
            </footer>
        </div>
    );
}
