package oth.ics.wtp.tweetgobackend.Dto;

import java.time.LocalDateTime;
import java.util.Set;

public class TweetDto {
    private Long id;
    private String text;
    private LocalDateTime timestamp;
    private String authorUsername;
    private int likesCount;
    private int commentsCount;
    private boolean likedByCurrentUser;
    private String imageUrl; // if you include images
    private Set<String> hashtags;

    public TweetDto() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }
    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public int getLikesCount() {
        return likesCount;
    }
    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }
    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }
    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Set<String> getHashtags() {
        return hashtags;
    }
    public void setHashtags(Set<String> hashtags) {
        this.hashtags = hashtags;
    }
}
