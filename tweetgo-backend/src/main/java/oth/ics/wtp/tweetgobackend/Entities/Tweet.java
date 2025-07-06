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

    @OneToMany(mappedBy = "tweet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tweet_hashtags",
            joinColumns = @JoinColumn(name = "tweet_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    private Set<Hashtag> hashtags = new HashSet<>();

    @Column
    private String imageUrl;

    public Tweet() { }

    public Tweet(String text, User author) {
        this.text = text;
        this.author = author;
    }

    @PrePersist
    public void onCreate() {
        this.timestamp = LocalDateTime.now();
    }


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

    public Set<Comment> getComments() {
        return comments;
    }
    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }
    public int getCommentsCount() {
        return comments.size();
    }
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setTweet(this);
    }
    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setTweet(null);
    }

    public Set<Hashtag> getHashtags() {
        return hashtags;
    }
    public void setHashtags(Set<Hashtag> hashtags) {
        this.hashtags = hashtags;
    }
    public void addHashtag(Hashtag hashtag) {
        hashtags.add(hashtag);
        hashtag.getTweets().add(this);
    }
    public void removeHashtag(Hashtag hashtag) {
        hashtags.remove(hashtag);
        hashtag.getTweets().remove(this);
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }



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
                ", commentsCount=" + getCommentsCount() +
                '}';
    }
}
