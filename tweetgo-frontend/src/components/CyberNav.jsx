// src/components/CyberNav.jsx
import { useState, useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../Context/AuthContext';
import {
    FiHome,
    FiSearch,
    FiUser,
    FiLogOut,
    FiMenu,
    FiX
} from 'react-icons/fi'; // <-- make sure you import the CSS below

export default function CyberNav() {
    const { logout } = useContext(AuthContext);
    const navigate = useNavigate();
    const [open, setOpen] = useState(false);

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav className="cyber-nav">
            <div className="container">
                <div className="brand-toggle">
                    <Link to="/feed" className="brand">
                        TWEETGO
                    </Link>
                    <button
                        className="toggle-btn"
                        onClick={() => setOpen(o => !o)}
                        aria-label="Toggle menu"
                    >
                        {open ? <FiX size={24}/> : <FiMenu size={24}/>}
                    </button>
                </div>

                <div className={`links ${open ? 'open' : ''}`}>
                    <Link to="/feed" className="link">
                        <FiHome /> Feed
                    </Link>
                    <Link to="/discover" className="link">
                        <FiSearch /> Search
                    </Link>
                    <Link to="/profile" className="link">
                        <FiUser /> Profile
                    </Link>
                    <button onClick={handleLogout} className="link logout">
                        <FiLogOut /> Logout
                    </button>
                </div>
            </div>
        </nav>
    );
}
