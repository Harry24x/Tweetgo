import React, { useState, useContext } from 'react';
import { AuthContext } from '../Context/AuthContext';
import TweetCard from '../components/TweetCard';
import axios from '../axios';
import CyberNav from '../components/CyberNav';

const DiscoverPage = () => {
  const { user } = useContext(AuthContext);
  const [searchQuery, setSearchQuery] = useState('');
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [userTweets, setUserTweets] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);
  const [searchLoading, setSearchLoading] = useState(false);

  const searchUsers = async () => {
    if (!searchQuery.trim()) return;
    setSearchLoading(true);
    try {
      const resp = await axios.get(
          `/api/users/search?q=${encodeURIComponent(searchQuery.trim())}`
      );
      setUsers(resp.data);
      setSelectedUser(null);
      setUserTweets([]);
      setCurrentPage(0);
      setHasMore(true);
    } catch (e) {
      console.error(e);
    }
    setSearchLoading(false);
  };

  const loadUserTweets = async (username, page = 0) => {
    if (loading) return;
    setLoading(true);
    try {
      const resp = await axios.get(`/api/tweet/user/${username}?page=${page}`);
      const tweets = resp.data;
      if (page === 0) {
        setUserTweets(tweets);
      } else {
        setUserTweets(prev => [...prev, ...tweets]);
      }
      setHasMore(tweets.length === 20);
    } catch (e) {
      console.error(e);
    }
    setLoading(false);
  };

  const handleUserSelect = async u => {
    setSelectedUser(u);
    setCurrentPage(0);
    setUserTweets([]);
    await loadUserTweets(u.username, 0);
  };

  const handleFollow = async uname => {
    try {
      await axios.post(`/api/users/${uname}/follow`);
      setUsers(prev =>
          prev.map(u =>
              u.username === uname
                  ? { ...u, followedByCurrentUser: true, followersCount: u.followersCount + 1 }
                  : u
          )
      );
      if (selectedUser?.username === uname) {
        setSelectedUser(s => ({
          ...s,
          followedByCurrentUser: true,
          followersCount: s.followersCount + 1,
        }));
      }
    } catch (e) {
      console.error(e);
    }
  };

  const handleUnfollow = async uname => {
    try {
      await axios.post(`/api/users/${uname}/unfollow`);
      setUsers(prev =>
          prev.map(u =>
              u.username === uname
                  ? { ...u, followedByCurrentUser: false, followersCount: u.followersCount - 1 }
                  : u
          )
      );
      if (selectedUser?.username === uname) {
        setSelectedUser(s => ({
          ...s,
          followedByCurrentUser: false,
          followersCount: s.followersCount - 1,
        }));
      }
    } catch (e) {
      console.error(e);
    }
  };

  const handleLoadMore = () => {
    if (!selectedUser) return;
    const next = currentPage + 1;
    setCurrentPage(next);
    loadUserTweets(selectedUser.username, next);
  };

  const handleLike = tweetId => {
    setUserTweets(prev =>
        prev.map(t =>
            t.id === tweetId
                ? { ...t, likedByCurrentUser: true, likesCount: t.likesCount + 1 }
                : t
        )
    );
  };

  const handleUnlike = tweetId => {
    setUserTweets(prev =>
        prev.map(t =>
            t.id === tweetId
                ? { ...t, likedByCurrentUser: false, likesCount: t.likesCount - 1 }
                : t
        )
    );
  };

  const handleDelete = tweetId => {
    setUserTweets(prev => prev.filter(t => t.id !== tweetId));
  };

  return (<div className="container mt-4">
      <>
        <CyberNav />
        <div className="container py-4">
          <h1 className="display-4 mb-2">ꜱᴇᴀʀᴄʜ</h1>
          <p className="text-muted mb-4">PRESS SEARCH TO FIND USERNAME</p>

          <div className="input-group mb-4">
            <input
                type="text"
                className="form-control"
                placeholder="Search users..."
                value={searchQuery}
                onChange={e => setSearchQuery(e.target.value)}
                onKeyPress={e => e.key === 'Enter' && searchUsers()}
            />
            <button
                type="button"
                className="btn btn-primary"
                onClick={searchUsers}
                disabled={!searchQuery.trim() || searchLoading}
            >
              {searchLoading ? 'Searching…' : 'Search'}
            </button>
          </div>

          <div className="row">
            <div className="col-md-4 mb-4">
              <h5>Users</h5>
              {users.length === 0 && searchQuery && !searchLoading && (
                  <div className="alert alert-warning">No users found</div>
              )}
              {users.map(u => (
                  <div key={u.username} className="card mb-2">
                    <div className="card-body d-flex justify-content-between align-items-center">
                      <div>
                        <strong>@{u.username}</strong>
                        <div className="text-muted small">
                          {u.followersCount} followers · {u.followingCount} following
                        </div>
                      </div>
                      <div>
                        <button
                            className="btn btn-sm btn-outline-secondary me-2"
                            onClick={() => handleUserSelect(u)}
                        >
                          View
                        </button>
                        {u.username !== user?.username && (
                            <button
                                className={
                                  u.followedByCurrentUser
                                      ? 'btn btn-sm btn-danger'
                                      : 'btn btn-sm btn-success'
                                }
                                onClick={() =>
                                    u.followedByCurrentUser
                                        ? handleUnfollow(u.username)
                                        : handleFollow(u.username)
                                }
                            >
                              {u.followedByCurrentUser ? 'Unfollow' : 'Follow'}
                            </button>
                        )}
                      </div>
                    </div>
                  </div>
              ))}
            </div>

            <div className="col-md-8">
              {selectedUser && (
                  <>
                    <div className="d-flex justify-content-between align-items-center mb-3">
                      <div>
                        <h4>@{selectedUser.username}</h4>
                        <div className="text-muted small">
                          {selectedUser.followersCount} followers ·{' '}
                          {selectedUser.followingCount} following
                        </div>
                      </div>
                      {selectedUser.username !== user?.username && (
                          <button
                              className={
                                selectedUser.followedByCurrentUser
                                    ? 'btn btn-danger'
                                    : 'btn btn-success'
                              }
                              onClick={() =>
                                  selectedUser.followedByCurrentUser
                                      ? handleUnfollow(selectedUser.username)
                                      : handleFollow(selectedUser.username)
                              }
                          >
                            {selectedUser.followedByCurrentUser ? 'Unfollow' : 'Follow'}
                          </button>
                      )}
                    </div>

                    <h5>Tweets</h5>
                    {userTweets.length === 0 && !loading && (
                        <p className="text-muted">
                          @{selectedUser.username} hasn’t posted any tweets yet.
                        </p>
                    )}
                    {userTweets.map(t => (
                        <TweetCard
                            key={t.id}
                            tweet={t}
                            onLike={handleLike}
                            onUnlike={handleUnlike}
                            onDelete={handleDelete}
                            showDelete={user?.username === t.authorUsername}
                        />
                    ))}

                    {hasMore && (
                        <div className="d-grid mt-3">
                          <button
                              className="btn btn-outline-primary"
                              onClick={handleLoadMore}
                              disabled={loading}
                          >
                            {loading ? 'Loading…' : 'Load More'}
                          </button>
                        </div>
                    )}
                  </>
              )}
            </div>
          </div>
        </div>
      </>
  </div>  );
};

export default DiscoverPage;
