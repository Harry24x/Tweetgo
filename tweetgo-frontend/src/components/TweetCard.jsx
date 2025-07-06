import React, { useState } from 'react';
import axios from '../axios';
import { useNavigate } from 'react-router-dom';

const TweetCard = ({ tweet, onDelete, onLike, onUnlike, showDelete }) => {
  const [showComments, setShowComments] = useState(false);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [commentPage, setCommentPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const formatDate = (timestamp) => {
    return new Date(timestamp).toLocaleString();
  };

  const handleLike = async () => {
    try {
      if (tweet.likedByCurrentUser) {
        await axios.delete(`/api/tweet/${tweet.id}/unlike`);
        onUnlike(tweet.id);
      } else {
        await axios.post(`/api/tweet/${tweet.id}/like`);
        onLike(tweet.id);
      }
    } catch (error) {
      console.error('Error toggling like:', error);
    }
  };

  const loadComments = async () => {
    if (!showComments) {
      setLoading(true);
      try {
        const response = await axios.get(`/api/tweet/${tweet.id}/comments?page=0`);
        setComments(response.data);
        setCommentPage(0);
      } catch (error) {
        console.error('Error loading comments:', error);
      }
      setLoading(false);
    }
    setShowComments(!showComments);
  };

  const loadMoreComments = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`/api/tweet/${tweet.id}/comments?page=${commentPage + 1}`);
      setComments([...comments, ...response.data]);
      setCommentPage(commentPage + 1);
    } catch (error) {
      console.error('Error loading more comments:', error);
    }
    setLoading(false);
  };

  const handleAddComment = async () => {
    if (!newComment.trim()) return;
    
    try {
      const response = await axios.post(`/api/tweet/${tweet.id}/comments`, {
        text: newComment
      });
      setComments([response.data, ...comments]);
      setNewComment('');
    } catch (error) {
      console.error('Error adding comment:', error);
    }
  };

  const renderHashtags = (text) => {
    const hashtagRegex = /#(\w+)/g;
    const parts = text.split(hashtagRegex);
    const result = [];
    
    for (let i = 0; i < parts.length; i++) {
      if (i % 2 === 0) {
        result.push(<span key={i}>{parts[i]}</span>);
      } else {
        result.push(
          <span key={i} className="hashtag" style={{ color: '#0000ff', fontWeight: 'bold' }}>
            #{parts[i]}
          </span>
        );
      }
    }
    
    return result;
  };

  const handleUsernameClick = (e) => {
    e.stopPropagation();
    navigate(`/profile?user=${tweet.authorUsername}`);
  };

  const handleTweetClick = () => {
    navigate(`/tweet/${tweet.id}`);
  };

  return (
    <div className="tweet-card" onClick={handleTweetClick} style={{ cursor: 'pointer' }}>
      <div className="tweet-header">
        <span className="tweet-username" onClick={handleUsernameClick} style={{ color: '#0000ff', cursor: 'pointer' }}>
          @{tweet.authorUsername}
        </span>
        <span className="tweet-date">{formatDate(tweet.timestamp)}</span>
        {showDelete && (
          <button className="delete-btn" onClick={e => { e.stopPropagation(); onDelete(tweet.id); }}>
            🗑
          </button>
        )}
      </div>
      <div className="tweet-content">
        {renderHashtags(tweet.text)}
      </div>
      {tweet.imageUrl && (
        <img src={tweet.imageUrl} alt="Tweet" style={{ maxWidth: '100%', borderRadius: '8px' }} />
      )}
      <div className="tweet-actions">
        <span className="tweet-likes" onClick={handleLike} style={{ color: tweet.likedByCurrentUser ? '#ff0000' : '#fff', cursor: 'pointer' }}>
          ♥ {tweet.likesCount}
        </span>
        <span className="tweet-comments" onClick={loadComments} style={{ cursor: 'pointer' }}>
          💬 {tweet.commentsCount}
        </span>
      </div>

      {showComments && (
        <div className="comments-section">
          <div className="add-comment">
            <textarea
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              placeholder="Add a comment..."
              maxLength={500}
              style={{
                width: '100%',
                minHeight: '60px',
                padding: '8px',
                border: '1px solid #00ff41',
                borderRadius: '4px',
                background: '#0a0a0a',
                color: '#ffffff',
                resize: 'vertical'
              }}
            />
            <button 
              onClick={handleAddComment}
              disabled={!newComment.trim()}
              style={{
                marginTop: '8px',
                padding: '8px 16px',
                background: '#0000ff',
                color: '#000000',
                border: 'none',
                borderRadius: '4px',
                cursor: newComment.trim() ? 'pointer' : 'not-allowed',
                fontWeight: 'bold'
              }}
            >
              COMMENT
            </button>
          </div>
          
          <div className="comments-list">
            {loading && <div className="loading">Loading comments...</div>}
            {comments.map((comment) => (
              <div key={comment.id} className="comment">
                <div className="comment-header">
                  <span className="comment-author">@{comment.authorUsername}</span>
                  <span className="comment-timestamp">{formatDate(comment.timestamp)}</span>
                </div>
                <p className="comment-text">{comment.text}</p>
              </div>
            ))}
            {comments.length > 0 && comments.length % 20 === 0 && (
              <button 
                onClick={loadMoreComments}
                disabled={loading}
                style={{
                  width: '100%',
                  padding: '8px',
                  background: 'none',
                  border: '1px solid #00ff41',
                  color: '#0000ff',
                  borderRadius: '4px',
                  cursor: loading ? 'not-allowed' : 'pointer',
                  marginTop: '8px'
                }}
              >
                {loading ? 'Loading...' : 'Load More Comments'}
              </button>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default TweetCard;
