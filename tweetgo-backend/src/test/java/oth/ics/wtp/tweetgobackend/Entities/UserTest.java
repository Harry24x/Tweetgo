package oth.ics.wtp.tweetgobackend.Entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("alice", "secret");
    }

    @Test
    void testNoArgConstructorInitialCollections() {
        User u = new User();
        assertNotNull(u.getPosts(), "posts list must be initialized");
        assertTrue(u.getPosts().isEmpty(), "posts should start empty");

        assertNotNull(u.getFollowing(), "following set must be initialized");
        assertTrue(u.getFollowing().isEmpty(), "following should start empty");

        assertNotNull(u.getFollowers(), "followers set must be initialized");
        assertTrue(u.getFollowers().isEmpty(), "followers should start empty");
    }

    @Test
    void testGettersAndSetters() {

        user.setId(123L);
        assertEquals(123L, user.getId());
        user.setUsername("bob");
        assertEquals("bob", user.getUsername());

        user.setPassword("newpass");
        assertEquals("newpass", user.getPassword());
    }

    @Test
    void testAddAndRemovePost() {
        Tweet t = new Tweet("hi", user);
        user.addPost(t);
        assertEquals(1, user.getPosts().size());
        assertTrue(user.getPosts().contains(t));
        assertEquals(user, t.getAuthor());
        user.removePost(t);
        assertTrue(user.getPosts().isEmpty());
        assertNull(t.getAuthor());
    }

    @Test
    void testRemovePostNotPresent() {
        Tweet t = new Tweet("nope", user);
        assertDoesNotThrow(() -> user.removePost(t));
        assertTrue(user.getPosts().isEmpty());
    }

    @Test
    void testFollowAndUnfollow() {
        User other = new User("charlie", "pw");
        user.followUser(other);
        assertTrue(user.getFollowing().contains(other),
                "after followUser, other should be in following set");
        assertTrue(other.getFollowers().contains(user),
                "after followUser, this user should appear in other's followers set");
        user.unfollowUser(other);
        assertFalse(user.getFollowing().contains(other),
                "after unfollowUser, other should no longer be in following set");
        assertFalse(other.getFollowers().contains(user),
                "after unfollowUser, this user should no longer be in other's followers set");
    }

    @Test
    void testUnfollowWhenNotFollowing() {
        User other = new User("dan", "pw");
        assertDoesNotThrow(() -> user.unfollowUser(other));
        assertTrue(user.getFollowing().isEmpty());
        assertTrue(other.getFollowers().isEmpty());
    }

    @Test
    void testUserDetailsDefaults() {
        Collection<? extends GrantedAuthority> auth = user.getAuthorities();
        assertNotNull(auth);
        assertTrue(auth.isEmpty());

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }
}
