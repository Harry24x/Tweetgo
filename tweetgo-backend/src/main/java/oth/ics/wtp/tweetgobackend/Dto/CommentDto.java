package oth.ics.wtp.tweetgobackend.Dto;

import java.time.LocalDateTime;

public class CommentDto {
    private Long id;
    private String text;
    private LocalDateTime timestamp;
    private String authorUsername;

    public CommentDto() {}

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
} 