import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import axios from "../axios";
import CyberNav from "../components/CyberNav";
import TweetCard from "../components/TweetCard";

function useQuery() {
  return new URLSearchParams(useLocation().search);
}

const ProfilePage = () => {
  const query = useQuery();
  const usernameParam = query.get("user");    // ?user=someone
  const [profile, setProfile] = useState(null);
  const [tweets, setTweets] = useState([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);

  // --- EDIT PROFILE STATES ---
  const [editMode, setEditMode] = useState(false);
  const [editValues, setEditValues] = useState({
    fullName: "",
    address: "",
    bio: ""
  });
  const isOwnProfile = !usernameParam || usernameParam === profile?.username;

  // Fetch profile whenever usernameParam changes
  useEffect(() => {
    fetchProfile(usernameParam);
    setPage(0);
    setTweets([]);
  }, [usernameParam]);

  // Fetch tweets whenever profile or page changes
  useEffect(() => {
    if (profile) fetchTweets(profile.username, page);
  }, [profile, page]);

  // Fetch profile data + seed editValues
  const fetchProfile = async (uname) => {
    try {
      const res = await axios.get(
          `/api/users/${uname || "me"}`
      );
      const data = res.data;
      setProfile(data);
      setEditValues({
        fullName: data.fullName || "",
        address:  data.address  || "",
        bio:      data.bio      || ""
      });
    } catch {
      setProfile(null);
    }
  };

  // Fetch paginated tweets
  const fetchTweets = async (username, pg) => {
    if (loading) return;
    setLoading(true);
    try {
      const res = await axios.get(
          `/api/tweet/user/${username}?page=${pg}`
      );
      const list = res.data;
      setTweets(pg === 0 ? list : prev => [...prev, ...list]);
      setHasMore(list.length === 20);
    } catch {
      /* ignore */
    }
    setLoading(false);
  };

  // Delete handler
  const handleDelete = (tweetId) =>
      setTweets(prev => prev.filter(t => t.id !== tweetId));

  // Load more tweets
  const handleLoadMore = () => setPage(p => p + 1);

  // Save edits
  const handleEditSave = async () => {
    try {
      await axios.put('/api/users/me', {
        fullName: editValues.fullName,
        address:  editValues.address,
        bio:      editValues.bio
      });
    } catch (err) {
      console.error("Failed to update profile:", err);
    }
  };

  if (!profile) {
    return (
        <div className="profile-page">
          <CyberNav />
          <div className="container py-5 text-center">
            <h2>Loading profile…</h2>
          </div>
        </div>
    );
  }

  return (
      <div className="profile-page">
        <CyberNav />
        <div className="container py-4">

          {/* HEADER */}
          <div className="d-flex justify-content-between align-items-center mb-4">
            <h1>@{profile.username}</h1>
            {isOwnProfile && (
                <button
                    className="btn btn-outline-primary"
                    onClick={() => setEditMode(true)}
                >
                  Edit Profile
                </button>
            )}
          </div>

          {/* PROFILE INFO */}
          <div className="card mb-4">
            <div className="card-body">
              {profile.fullName && <h5 className="card-title">{profile.fullName}</h5>}
              {profile.address  && <p className="card-text">{profile.address}</p>}
              {profile.bio      && <p className="card-text"><em>{profile.bio}</em></p>}
              <div className="mt-3 text-muted">
              <span className="me-4">
                <strong>{profile.followersCount}</strong> Followers
              </span>
                <span>
                <strong>{profile.followingCount}</strong> Following
              </span>
              </div>
            </div>
          </div>

          {/* EDIT PROFILE FORM */}
          {editMode && (
              <div className="card mb-4">
                <div className="card-body">
                  <h4>Edit Profile</h4>
                  <div className="mb-3">
                    <label className="form-label">Full Name</label>
                    <input
                        className="form-control"
                        value={editValues.fullName}
                        onChange={e => setEditValues(v => ({ ...v, fullName: e.target.value }))}
                    />
                  </div>
                  <div className="mb-3">
                    <label className="form-label">Address</label>
                    <input
                        className="form-control"
                        value={editValues.address}
                        onChange={e => setEditValues(v => ({ ...v, address: e.target.value }))}
                    />
                  </div>
                  <div className="mb-3">
                    <label className="form-label">Bio</label>
                    <textarea
                        className="form-control"
                        rows="3"
                        value={editValues.bio}
                        onChange={e => setEditValues(v => ({ ...v, bio: e.target.value }))}
                    />
                  </div>
                  <button
                      className="btn btn-primary me-2"
                      onClick={handleEditSave}
                  >
                    Save
                  </button>
                  <button
                      className="btn btn-secondary"
                      onClick={() => setEditMode(false)}
                  >
                    Cancel
                  </button>
                </div>
              </div>
          )}

          {/* TWEETS LIST */}
          <h3 className="mb-3">Tweets</h3>
          {tweets.map(t => (
              <TweetCard
                  key={t.id}
                  tweet={t}
                  onDelete={handleDelete}
                  showDelete={isOwnProfile}
              />
          ))}

          {hasMore && (
              <div className="text-center my-4">
                <button
                    className="btn btn-outline-primary"
                    onClick={handleLoadMore}
                    disabled={loading}
                >
                  {loading ? "Loading…" : "Load More"}
                </button>
              </div>
          )}

          {tweets.length === 0 && !loading && (
              <p className="text-center text-muted">No tweets yet.</p>
          )}

        </div>
      </div>
  );
};

export default ProfilePage;
