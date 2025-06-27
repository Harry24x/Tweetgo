package oth.ics.wtp.tweetgobackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tweets")
public class Tweet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tweet text cannot be blank")
    @Size(max = 280, message = "Tweet cannot exceed 280 characters")
    @Column(nullable = false, length = 280)
    private String text;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tweet_likes",
            joinColumns = @JoinColumn(name = "tweet_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likedByUsers = new HashSet<>();

    @Column
    private String imageUrl;

    public Tweet() { }

    public Tweet(String text, User author) {
        this.text = text;
        this.author = author;
    }

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

    // --- Getters & Setters ---

    public Long getId() {
        return id;
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

    public User getAuthor() {
        return author;
    }
    public void setAuthor(User author) {
        this.author = author;
    }

    public Set<User> getLikedByUsers() {
        return likedByUsers;
    }
    public int getLikesCount() {
        return likedByUsers.size();
    }
    public void addLike(User user) {
        likedByUsers.add(user);
    }
    public void removeLike(User user) {
        likedByUsers.remove(user);
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // --- equals/hashCode based on id ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tweet other = (Tweet) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                ", author=" + (author != null ? author.getUsername() : "null") +
                ", likesCount=" + getLikesCount() +
                '}';
    }
}
