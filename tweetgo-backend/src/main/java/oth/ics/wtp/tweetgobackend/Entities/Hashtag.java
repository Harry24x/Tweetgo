package oth.ics.wtp.tweetgobackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "hashtags", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Hashtag name cannot be blank")
    @Size(max = 50, message = "Hashtag name cannot exceed 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @ManyToMany(mappedBy = "hashtags")
    private Set<Tweet> tweets = new HashSet<>();

    public Hashtag() { }

    public Hashtag(String name) {
        this.name = name;
    }



    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Set<Tweet> getTweets() {
        return tweets;
    }
    public void setTweets(Set<Tweet> tweets) {
        this.tweets = tweets;
    }



    public void addTweet(Tweet tweet) {
        tweets.add(tweet);
        tweet.getHashtags().add(this);
    }

    public void removeTweet(Tweet tweet) {
        tweets.remove(tweet);
        tweet.getHashtags().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hashtag other = (Hashtag) o;
        return Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Hashtag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
} 