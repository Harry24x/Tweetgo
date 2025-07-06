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
import oth.ics.wtp.tweetgobackend.Dto.*;
import oth.ics.wtp.tweetgobackend.services.TweetService;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TweetControllerTest {

    @Mock
    private TweetService tweetService;

    @InjectMocks
    private TweetController controller;

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
    @DisplayName("POST /api/tweet → 201 + body on success")
    void createTweet_success() throws Exception {
        // prepare input DTO
        TweetCreationDto in = new TweetCreationDto();
        in.setText("Hello world!");

        // prepare service mocks
        TweetDto outDto = new TweetDto();
        outDto.setText("Hello world!");
        outDto.setAuthorUsername("alice");
        outDto.setLikesCount(0);
        outDto.setCommentsCount(0);
        outDto.setLikedByCurrentUser(false);
        outDto.setImageUrl(null);
        outDto.setHashtags(Set.of());
        outDto.setId(42L);

        when(tweetService.createtweet(any(), eq("alice"), isNull())).thenReturn(null); // we don't use returned Tweet
        when(tweetService.maptweetToDto(any(), isNull())).thenReturn(outDto);

        mockMvc.perform(post("/api/tweet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(auth("alice"))
                        .content(mapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("Hello world!"))
                .andExpect(jsonPath("$.authorUsername").value("alice"))
                .andExpect(jsonPath("$.likesCount").value(0))
                .andExpect(jsonPath("$.commentsCount").value(0))
                .andExpect(jsonPath("$.likedByCurrentUser").value(false));
    }

    @Test
    @DisplayName("POST /api/tweet → 400 on service error")
    void createTweet_failure() throws Exception {
        TweetCreationDto in = new TweetCreationDto();
        in.setText("🤦‍♂️");

        when(tweetService.createtweet(any(), eq("bob"), isNull()))
                .thenThrow(new IllegalArgumentException("bad input"));

        mockMvc.perform(post("/api/tweet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(auth("bob"))
                        .content(mapper.writeValueAsString(in)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("Error creating tweet: bad input")));
    }

    @Test
    @DisplayName("GET /api/tweet/feed → 200 + empty list")
    void getFeed_empty() throws Exception {
        when(tweetService.getAggregatedTimeline("alice", 0)).thenReturn(List.of());

        mockMvc.perform(get("/api/tweet/feed")
                        .principal(auth("alice")))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /api/tweet/feed → 200 + non-empty list")
    void getFeed_nonEmpty() throws Exception {
        TweetDto dto = new TweetDto();
        dto.setText("X");
        dto.setAuthorUsername("alice");
        when(tweetService.getAggregatedTimeline("alice", 2))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/tweet/feed")
                        .param("page", "2")
                        .principal(auth("alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].authorUsername").value("alice"))
                .andExpect(jsonPath("$[0].text").value("X"));
    }

    @Test
    @DisplayName("GET /api/tweet/user/{u} → 200")
    void getByUser() throws Exception {
        TweetDto dto = new TweetDto();
        dto.setText("BYE");
        dto.setAuthorUsername("charlie");
        when(tweetService.gettweetsByUsername("charlie", "bob", 1))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/tweet/user/charlie")
                        .param("page", "1")
                        .principal(auth("bob")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].authorUsername").value("charlie"))
                .andExpect(jsonPath("$[0].text").value("BYE"));
    }

    @Test
    @DisplayName("GET /api/tweet/{id} → 200")
    void getTweetById() throws Exception {
        TweetDto dto = new TweetDto();
        dto.setText("HI");
        dto.setAuthorUsername("dan");
        when(tweetService.getTweetById(77L, "ed")).thenReturn(dto);

        mockMvc.perform(get("/api/tweet/77")
                        .principal(auth("ed")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorUsername").value("dan"))
                .andExpect(jsonPath("$.text").value("HI"));
    }

    @Test
    @DisplayName("DELETE /api/tweet/{id} → 200")
    void deleteTweet() throws Exception {
        doNothing().when(tweetService).deleteTweet(5L, "sam");

        mockMvc.perform(delete("/api/tweet/5")
                        .principal(auth("sam")))
                .andExpect(status().isOk())
                .andExpect(content().string("Tweet deleted successfully."));

        verify(tweetService).deleteTweet(5L, "sam");
    }

    @Test
    @DisplayName("POST /api/tweet/{id}/like → 200")
    void likeTweet() throws Exception {
        doNothing().when(tweetService).liketweet(9L, "tom");

        mockMvc.perform(post("/api/tweet/9/like")
                        .principal(auth("tom")))
                .andExpect(status().isOk())
                .andExpect(content().string("Tweet liked successfully."));

        verify(tweetService).liketweet(9L, "tom");
    }

    @Test
    @DisplayName("DELETE /api/tweet/{id}/unlike → 200")
    void unlikeTweet() throws Exception {
        doNothing().when(tweetService).unliketweet(9L, "tom");

        mockMvc.perform(delete("/api/tweet/9/unlike")
                        .principal(auth("tom")))
                .andExpect(status().isOk())
                .andExpect(content().string("Tweet unliked successfully."));

        verify(tweetService).unliketweet(9L, "tom");
    }

    @Test
    @DisplayName("POST /api/tweet/{id}/comments → 201")
    void addComment() throws Exception {
        CommentCreationDto in = new CommentCreationDto();
        in.setText("Nice!");
        CommentDto out = new CommentDto();
        out.setId(123L);
        out.setText("Nice!");
        out.setAuthorUsername("uma");

        when(tweetService.addComment(3L, in, "uma")).thenReturn(out);

        mockMvc.perform(post("/api/tweet/3/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(auth("uma"))
                        .content(mapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.text").value("Nice!"))
                .andExpect(jsonPath("$.authorUsername").value("uma"));
    }

    @Test
    @DisplayName("GET /api/tweet/{id}/comments → 200")
    void getComments() throws Exception {
        CommentDto c = new CommentDto();
        c.setId(5L);
        c.setText("Foo");
        c.setAuthorUsername("vic");
        when(tweetService.getCommentsByTweetId(8L, 2))
                .thenReturn(List.of(c));

        mockMvc.perform(get("/api/tweet/8/comments")
                        .param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].text").value("Foo"))
                .andExpect(jsonPath("$[0].authorUsername").value("vic"));
    }

    @Test
    @DisplayName("GET /api/tweet/hashtag/{tag} → 200")
    void getTweetsByHashtag() throws Exception {
        TweetDto dto = new TweetDto();
        dto.setText("tagged");
        dto.setAuthorUsername("walt");
        when(tweetService.getTweetsByHashtag("fun", "mia", 0))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/tweet/hashtag/fun")
                        .param("page", "0")
                        .principal(auth("mia")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].authorUsername").value("walt"))
                .andExpect(jsonPath("$[0].text").value("tagged"));
    }
}
