package oth.ics.wtp.tweetgobackend.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    @JsonIgnore
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Sent to the frontend as an ISO instant with time zone (e.g. 2026-11-18T09:30:00Z),
    // so the browser always shows the correct local time.
    @JsonProperty("timestamp")
    public Instant getTimestampInstant() {
        return timestamp == null ? null : timestamp.atZone(ZoneId.systemDefault()).toInstant();
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
