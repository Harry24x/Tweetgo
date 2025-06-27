package oth.ics.wtp.tweetgobackend.Dto;

import java.time.LocalDateTime;

public class TweetDto {
    private Long id;
    private String text;
    private LocalDateTime timestamp;
    private String authorUsername;
    private int likesCount;
    private boolean likedByCurrentUser;
    private String imageUrl; // if you include images

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
}
