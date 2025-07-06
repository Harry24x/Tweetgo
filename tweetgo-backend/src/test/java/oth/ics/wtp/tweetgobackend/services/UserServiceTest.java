package oth.ics.wtp.tweetgobackend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import oth.ics.wtp.tweetgobackend.Dto.UserDto;
import oth.ics.wtp.tweetgobackend.Entities.User;
import oth.ics.wtp.tweetgobackend.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService svc;

    private User alice;
    private User bob;

    @BeforeEach
    void setUp() {
        alice = new User("alice", "pw");
        alice.setId(1L);
        bob   = new User("bob",   "pw2");
        bob.setId(2L);
    }

    @Test @DisplayName("usernameExists → true/false")
    void usernameExists() {
        when(userRepo.existsByUsername("alice")).thenReturn(true);
        when(userRepo.existsByUsername("charlie")).thenReturn(false);

        assertTrue(svc.usernameExists("alice"));
        assertFalse(svc.usernameExists("charlie"));
    }

    @Test @DisplayName("registerUser → encodes & saves")
    void registerUser() {
        when(passwordEncoder.encode("raw")).thenReturn("ENC");
        User saved = new User("new", "ENC");
        when(userRepo.save(any())).thenReturn(saved);

        User out = svc.registerUser("new", "raw");
        assertEquals("new", out.getUsername());
        assertEquals("ENC", out.getPassword());

        // ensure we passed encoded pw to repo
        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(cap.capture());
        assertEquals("ENC", cap.getValue().getPassword());
    }

    @Test @DisplayName("followUser → success")
    void followUser_success() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(alice));
        when(userRepo.findByUsername("bob")).thenReturn(Optional.of(bob));

        svc.followUser("alice", "bob");

        // relationship established
        assertTrue(alice.getFollowing().contains(bob));
        assertTrue(bob.getFollowers().contains(alice));
        // both sides saved
        verify(userRepo, times(1)).save(alice);
        verify(userRepo, times(1)).save(bob);
    }

    @Test @DisplayName("followUser → cannot follow self")
    void followUser_self() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(alice));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> svc.followUser("alice", "alice")
        );
        assertTrue(ex.getMessage().contains("cannot follow yourself"));
    }

    @Test @DisplayName("followUser → follower not found")
    void followUser_followerNotFound() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> svc.followUser("alice", "bob")
        );
        assertTrue(ex.getMessage().contains("User not found: alice"));
    }

    @Test @DisplayName("followUser → target not found")
    void followUser_targetNotFound() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(alice));
        when(userRepo.findByUsername("bob")).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> svc.followUser("alice", "bob")
        );
        assertTrue(ex.getMessage().contains("User not found: bob"));
    }

    @Test @DisplayName("unfollowUser → success")
    void unfollowUser_success() {
        // establish first
        alice.followUser(bob);
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(alice));
        when(userRepo.findByUsername("bob")).thenReturn(Optional.of(bob));

        svc.unfollowUser("alice", "bob");

        assertFalse(alice.getFollowing().contains(bob));
        assertFalse(bob.getFollowers().contains(alice));
        verify(userRepo).save(alice);
        verify(userRepo).save(bob);
    }

    @Test @DisplayName("searchUsers → filters current user")
    void searchUsers() {
        User charlie = new User("charlie","x");
        when(userRepo.findByUsernameContaining("a")).thenReturn(List.of(alice, bob, charlie));

        // alice is current → should remove alice from results
        List<UserDto> results = svc.searchUsers("a", "alice");
        assertEquals(2, results.size());
        Set<String> names = Set.of(results.get(0).getUsername(), results.get(1).getUsername());
        assertTrue(names.contains("bob") && names.contains("charlie"));
    }

    @Test @DisplayName("getUserProfile → self (no follow flag)")
    void getUserProfile_self() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(alice));
        UserDto dto = svc.getUserProfile("alice", "alice");
        assertEquals("alice", dto.getUsername());
        assertFalse(dto.isFollowedByCurrentUser());
    }

    @Test @DisplayName("getUserProfile → other, followed by current")
    void getUserProfile_other() {
        // bob follows alice
        bob.followUser(alice);
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(alice));
        when(userRepo.findByUsername("bob")).thenReturn(Optional.of(bob));

        UserDto dto = svc.getUserProfile("alice", "bob");
        assertTrue(dto.isFollowedByCurrentUser());
    }

    @Test @DisplayName("getUserProfile → not found")
    void getUserProfile_notFound() {
        when(userRepo.findByUsername("nobody")).thenReturn(Optional.empty());
        assertThrows(
                IllegalArgumentException.class,
                () -> svc.getUserProfile("nobody", "alice")
        );
    }
}
