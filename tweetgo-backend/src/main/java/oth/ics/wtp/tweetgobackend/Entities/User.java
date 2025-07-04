package oth.ics.wtp.tweetgobackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = "username")
)
public class User implements UserDetails {

    // --- Primary Key ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Basic fields ---

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * This should store the BCRYPT-hashed password.
     */
    @NotBlank
    @Column(nullable = false)
    private String password;

    /**
     * The user’s full name (optional).
     */
    @Size(max = 100)
    @Column(length = 100)
    private String fullName;

    @Email
    @Size(max = 100)
    @Column(length = 100)
    private String email;

    @Size(max = 250)
    @Column(length = 250)
    private String bio;


    @OneToMany(
            mappedBy      = "author",
            cascade       = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Tweet> posts = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name               = "user_following_map",
            joinColumns        = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    private Set<User> following = new HashSet<>();

    @ManyToMany(mappedBy = "following")
    private Set<User> followers = new HashSet<>();

    // --- Constructors ---


    public User() {
    }

    public User(String username, String passwordHash) {
        this.username = username;
        this.password = passwordHash;
    }


    public User(String username,
                String passwordHash,
                String fullName,
                String email) {
        this(username, passwordHash);
        this.fullName = fullName;
        this.email    = email;
    }

    // --- Getters & setters ---

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

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
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

    // --- Spring Security UserDetails methods ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // no roles/authorities for now
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
