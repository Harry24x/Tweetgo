import React from 'react';
import { Link } from 'react-router-dom';

const Homepage = () => {
    const isLoggedIn = !!localStorage.getItem('token');

    const handleLogout = () => {
        localStorage.removeItem('token');
        window.location.href = '/';
    };

    return (
        <div className="container mt-5 text-center">
            <h1>Welcome to TweetGo</h1>
            {!isLoggedIn ? (
                <div className="mt-4">
                    <Link to="/login" className="btn btn-success mx-2">Login</Link>
                    <Link to="/register" className="btn btn-primary mx-2">Register</Link>
                </div>
            ) : (
                <div className="mt-4">
                    <button className="btn btn-danger" onClick={handleLogout}>Logout</button>
                </div>
            )}
        </div>
    );
};

export default Homepage;