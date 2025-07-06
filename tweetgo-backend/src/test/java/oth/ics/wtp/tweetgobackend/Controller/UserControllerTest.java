package oth.ics.wtp.tweetgobackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import oth.ics.wtp.tweetgobackend.Dto.UserDto;
import oth.ics.wtp.tweetgobackend.services.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private UsernamePasswordAuthenticationToken auth(String username) {
        return new UsernamePasswordAuthenticationToken(username, "N/A");
    }

    @Test
    @DisplayName("GET /api/users/search?q=foo → 200 + list")
    void searchUsers() throws Exception {
        UserDto u1 = new UserDto();
        u1.setUsername("alice");
        u1.setFollowersCount(2);
        u1.setFollowingCount(3);
        u1.setFollowedByCurrentUser(true);

        when(userService.searchUsers("ali", "bob"))
                .thenReturn(List.of(u1));

        mockMvc.perform(get("/api/users/search")
                        .param("q", "ali")
                        .principal(auth("bob")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].username").value("alice"))
                .andExpect(jsonPath("$[0].followersCount").value(2))
                .andExpect(jsonPath("$[0].followingCount").value(3))
                .andExpect(jsonPath("$[0].followedByCurrentUser").value(true));

        verify(userService).searchUsers("ali", "bob");
    }

    @Test
    @DisplayName("POST /api/users/{me}/follow → 400 if following yourself")
    void follow_self_badRequest() throws Exception {
        mockMvc.perform(post("/api/users/john/follow")
                        .principal(auth("john")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("You cannot follow yourself."));

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("POST /api/users/{other}/follow → 200")
    void follow_other_success() throws Exception {
        doNothing().when(userService).followUser("alice", "bob");

        mockMvc.perform(post("/api/users/bob/follow")
                        .principal(auth("alice")))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully followed bob"));

        verify(userService).followUser("alice", "bob");
    }

    @Test
    @DisplayName("POST /api/users/{other}/unfollow → 200")
    void unfollow_success() throws Exception {
        doNothing().when(userService).unfollowUser("mark", "jane");

        mockMvc.perform(post("/api/users/jane/unfollow")
                        .principal(auth("mark")))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully unfollowed jane"));

        verify(userService).unfollowUser("mark", "jane");
    }

    @Test
    @DisplayName("GET /api/users/{username} → 200 + profile")
    void getUserProfile() throws Exception {
        UserDto dto = new UserDto();
        dto.setUsername("zoe");
        dto.setFollowersCount(5);
        dto.setFollowingCount(1);
        dto.setFollowedByCurrentUser(false);

        when(userService.getUserProfile("zoe", "amy")).thenReturn(dto);

        mockMvc.perform(get("/api/users/zoe")
                        .principal(auth("amy")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("zoe"))
                .andExpect(jsonPath("$.followersCount").value(5))
                .andExpect(jsonPath("$.followingCount").value(1))
                .andExpect(jsonPath("$.followedByCurrentUser").value(false));

        verify(userService).getUserProfile("zoe", "amy");
    }
}
