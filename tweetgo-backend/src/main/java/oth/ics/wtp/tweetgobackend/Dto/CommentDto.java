package oth.ics.wtp.tweetgobackend.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
} 