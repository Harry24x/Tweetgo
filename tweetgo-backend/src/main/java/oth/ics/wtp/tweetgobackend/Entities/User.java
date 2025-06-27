package oth.ics.wtp.tweetgobackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(min = 3, max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String password;

    // --- Relationships ---

    // A user’s own tweets
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tweet> posts = new ArrayList<>();

    // The users this user is following
    @ManyToMany
    @JoinTable(
            name = "user_following_map",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    private Set<User> following = new HashSet<>();

    // The users who follow this user
    @ManyToMany(mappedBy = "following")
    private Set<User> followers = new HashSet<>();

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // --- Accessors & Mutators ---

    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public List<Tweet> getPosts() {
        return posts;
    }

    public Set<User> getFollowing() {
        return following;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    // --- Helper methods ---

    public void addPost(Tweet post) {
        posts.add(post);
        post.setAuthor(this);
    }

    public void removePost(Tweet post) {
        posts.remove(post);
        post.setAuthor(null);
    }

    public void followUser(User other) {
        following.add(other);
        other.followers.add(this);
    }

    public void unfollowUser(User other) {
        following.remove(other);
        other.followers.remove(this);
    }

    // --- UserDetails methods ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
