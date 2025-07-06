import React, { useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom'
import axios from '../axios'
import TweetCard from '../components/TweetCard'
import CyberNav from '../components/CyberNav'

function useQuery() {
  return new URLSearchParams(useLocation().search)
}

export default function ProfilePage() {
  const query      = useQuery()
  const userParam  = query.get('user')
  const [profile, setProfile] = useState(null)
  const [tweets,  setTweets]  = useState([])
  const [page,    setPage]    = useState(0)
  const [hasMore, setHasMore] = useState(true)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setPage(0)
    setTweets([])
    fetchProfile(userParam)
  }, [userParam])

  useEffect(() => {
    if (profile) loadTweets(profile.username, page)
  }, [profile, page])

  async function fetchProfile(uname) {
    try {
      const res = await axios.get(`/api/users/${uname || 'me'}`)
      setProfile(res.data)
    } catch {
      setProfile(null)
    }
  }

  async function loadTweets(uname, pg) {
    if (!uname || loading) return
    setLoading(true)
    try {
      const res = await axios.get(
          `/api/tweet/user/${uname}?page=${pg}`
      )
      setTweets(t =>
          pg === 0 ? res.data : [...t, ...res.data]
      )
      setHasMore(res.data.length === 20)
    } catch { /* empty */ }
    setLoading(false)
  }

  if (!profile) {
    return (
        <>
          <CyberNav />
          <div className="container py-5 text-center text-secondary">
            Loading profile…
          </div>
        </>
    )
  }

  return (<div className="container mt-4">
      <>
        <CyberNav />

        <div className="bg-primary" style={{ height: '200px' }}>
        </div>
        <div className="container">
          <div className="row justify-content-center">
            <div className="col-auto">
              <div
                  className="rounded-circle border border-white"
                  style={{
                    width: '120px',
                    height: '120px',
                    marginTop: '-60px',
                    backgroundColor: '#ddd',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontSize: '36px',
                    color: '#555'
                  }}
              >
                {profile.username.charAt(0).toUpperCase()}
              </div>
            </div>
          </div>
        </div>

        {/* Profile info */}
        <div className="container mt-3">
          <div className="d-flex justify-content-between align-items-start">
            <div>
              <h2 className="mb-1">@{profile.username}</h2>
              <div className="text-muted">
              <span className="me-3">
                <strong>{profile.followersCount}</strong> followers
              </span>
                <span>
                <strong>{profile.followingCount}</strong> following
              </span>
              </div>
            </div>
            {!userParam || userParam === profile.username ? (
                <button className="btn btn-outline-primary">
                  Edit Profile
                </button>
            ) : null}
          </div>

          <hr />

          {/* Tweets list */}
          {tweets.map(t => (
              <TweetCard
                  key={t.id}
                  tweet={t}
                  onDelete={() =>
                      setTweets(ts => ts.filter(x => x.id !== t.id))
                  }
                  showDelete={
                      !userParam || userParam === profile.username
                  }
              />
          ))}

          {hasMore && (
              <div className="text-center my-4">
                <button
                    className="btn btn-primary"
                    onClick={() => setPage(p => p + 1)}
                    disabled={loading}
                >
                  {loading ? 'Loading…' : 'Load More'}
                </button>
              </div>
          )}

          {!loading && tweets.length === 0 && (
              <p className="text-center text-muted my-5">
                No tweets yet.
              </p>
          )}
        </div>
      </>
      </div>
  )
}
