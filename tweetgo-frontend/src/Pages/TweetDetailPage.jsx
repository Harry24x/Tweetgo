import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import CyberNav from '../components/CyberNav';
import axios from '../axios';

const TweetDetailPage = () => {
  const { id } = useParams();
  const [tweet, setTweet] = useState(null);
  const [comments, setComments] = useState([]);
  const [commentText, setCommentText] = useState('');
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);
  const [posting, setPosting] = useState(false);

  useEffect(() => {
    axios.get(`/api/tweet/${id}`)
        .then(res => setTweet(res.data))
        .catch(() => setTweet(null));
    setPage(0);
    setComments([]);
  }, [id]);

  useEffect(() => {
    if (!tweet) return;
    setLoading(true);
    axios.get(`/api/tweet/${id}/comments?page=${page}`)
        .then(res => {
          const data = res.data;
          setComments(prev => page === 0 ? data : [...prev, ...data]);
          setHasMore(data.length === 20);
        })
        .catch(console.error)
        .finally(() => setLoading(false));
  }, [tweet, page, id]);

  const handleAddComment = () => {
    if (!commentText.trim()) return;
    setPosting(true);
    axios.post(`/api/tweet/${id}/comments`, { text: commentText })
        .then(res => {
          setComments([res.data, ...comments]);
          setCommentText('');
        })
        .catch(console.error)
        .finally(() => setPosting(false));
  };

  const loadMore = () => setPage(p => p + 1);

  if (tweet === null) {
    return (
        <div className="container py-5">
          <CyberNav />
          <div className="text-center mt-5 text-muted">
            <h3>Loading tweet…</h3>
          </div>
        </div>
    );
  }

  return (<div className="container mt-4">
      <div className="container py-4">
        <CyberNav />

        <h1 className="mb-4 display-6">Tweet Detail</h1>

        {/* Tweet Card */}
        <div className="card mb-4 shadow-sm">
          <div className="card-body">
            <h5 className="card-title">@{tweet.authorUsername}</h5>
            <p className="card-text">{tweet.text}</p>

            {tweet.imageUrl && (
                <img
                    src={tweet.imageUrl}
                    alt="tweet"
                    className="img-fluid rounded mb-3"
                />
            )}

            <div className="d-flex justify-content-between text-muted small">
              <span>♥ {tweet.likesCount}</span>
              <span>💬 {tweet.commentsCount}</span>
              <span>{new Date(tweet.timestamp).toLocaleString()}</span>
            </div>
          </div>
        </div>

        <div className="mb-5">
          <h4>Comments</h4>


          <div className="input-group mb-3">
          <textarea
              className="form-control"
              placeholder="Add a comment…"
              value={commentText}
              onChange={e => setCommentText(e.target.value)}
              rows={2}
              maxLength={500}
          />
            <button
                className="btn btn-primary"
                type="button"
                disabled={!commentText.trim() || posting}
                onClick={handleAddComment}
            >
              {posting ? 'Posting…' : 'Comment'}
            </button>
          </div>


          {!loading && comments.length === 0 && (
              <p className="text-muted">No comments yet.</p>
          )}


          <ul className="list-group">
            {comments.map(c => (
                <li key={c.id} className="list-group-item">
                  <div className="d-flex justify-content-between">
                    <strong>@{c.authorUsername}</strong>
                    <small className="text-muted">
                      {new Date(c.timestamp).toLocaleString()}
                    </small>
                  </div>
                  <p className="mb-0">{c.text}</p>
                </li>
            ))}
          </ul>


          {hasMore && (
              <div className="d-grid mt-3">
                <button
                    className="btn btn-outline-secondary"
                    onClick={loadMore}
                    disabled={loading}
                >
                  {loading ? 'Loading…' : 'Load More Comments'}
                </button>
              </div>
          )}
        </div>
      </div>
      </div>
  );
};

export default TweetDetailPage;
