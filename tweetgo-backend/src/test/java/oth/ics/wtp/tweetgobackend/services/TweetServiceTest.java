package oth.ics.wtp.tweetgobackend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import oth.ics.wtp.tweetgobackend.Dto.*;
import oth.ics.wtp.tweetgobackend.Entities.*;
import oth.ics.wtp.tweetgobackend.repositories.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TweetServiceTest {

    @Mock TweetRepository tweetRepo;
    @Mock UserRepository userRepo;
    @Mock CommentRepository commentRepo;
    @Mock HashtagRepository hashtagRepo;
    @Mock FileStorageService fileStorageService;
    @InjectMocks TweetService svc;

    User alice, bob;
    Tweet tweet;

    @BeforeEach
    void setUp() {
        alice = new User("alice","pw");
        alice.setId(1L);
        bob   = new User("bob","pw2");
        bob.setId(2L);

        tweet = new Tweet("Hello #Test", alice);
        tweet.setId(10L);
        tweet.onCreate();
    }

    @Test
    @DisplayName("createTweet: no image, new hashtags")
    void createTweet_noImage_newHashtag() {
        var dto = new TweetCreationDto();
        dto.setText("Check this #Fun");

        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(alice));
        // hashtagRepo returns empty -> we save a new one
        when(hashtagRepo.findByName("fun")).thenReturn(Optional.empty());
        Hashtag saved = new Hashtag("fun");
        when(hashtagRepo.save(any())).thenReturn(saved);
        when(tweetRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Tweet created = svc.createtweet(dto, "alice", null);

        assertEquals("Check this #Fun", created.getText());
        assertEquals(alice, created.getAuthor());
        assertEquals(1, created.getHashtags().size());
        assertTrue(created.getHashtags().stream().anyMatch(h->h.getName().equals("fun")));

        verify(fileStorageService, never()).storeFile(any());
    }

    @Test
    @DisplayName("createTweet: with image")
    void createTweet_withImage() {
        var dto = new TweetCreationDto();
        dto.setText("Image post");

        MultipartFile img = mock(MultipartFile.class);
        when(img.isEmpty()).thenReturn(false);

        when(fileStorageService.storeFile(img)).thenReturn("uuid.png");
        when(userRepo.findByUsername("bob")).thenReturn(Optional.of(bob));
        when(tweetRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Tweet t = svc.createtweet(dto, "bob", img);

        assertEquals("/uploads/uuid.png", t.getImageUrl());
        verify(fileStorageService).storeFile(img);
    }

    @Test
    @DisplayName("mapTweetToDto")
    void mapTweetToDto() {
        tweet.addLike(bob);
        Hashtag tag = new Hashtag("test");
        tweet.addHashtag(tag);
        TweetDto dto = svc.maptweetToDto(tweet, bob);

        assertEquals(tweet.getText(), dto.getText());
        assertEquals(tweet.getLikesCount(), dto.getLikesCount());
        assertTrue(dto.isLikedByCurrentUser());
        assertEquals(Set.of("test"), dto.getHashtags());
    }

    @Test
    @DisplayName("getAggregatedTimeline: empty following → empty list")
    void getAggregatedTimeline_empty() {
        alice.getFollowing().clear();
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(alice));

        var list = svc.getAggregatedTimeline("alice", 0);
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("getAggregatedTimeline: with following")
    void getAggregatedTimeline_nonEmpty() {
        alice.getFollowing().add(bob);
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(alice));

        List<Tweet> tweets = List.of(tweet);
        when(tweetRepo.findByAuthorInOrderByTimestampDesc(
                eq(List.of(bob)),
                eq(PageRequest.of(1,20))
        )).thenReturn(tweets);

        var dtos = svc.getAggregatedTimeline("alice", 1);
        assertEquals(1, dtos.size());
        assertEquals(tweet.getText(), dtos.get(0).getText());
    }

    @Test
    @DisplayName("likeTweet: success")
    void likeTweet_success() {
        when(tweetRepo.findById(10L)).thenReturn(Optional.of(tweet));
        when(userRepo.findByUsername("bob")).thenReturn(Optional.of(bob));

        svc.liketweet(10L, "bob");
        assertTrue(tweet.getLikedByUsers().contains(bob));
    }

    @Test
    @DisplayName("likeTweet: can't like own tweet")
    void likeTweet_ownTweet() {
        when(tweetRepo.findById(10L)).thenReturn(Optional.of(tweet));
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(alice));

        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> svc.liketweet(10L,"alice")
        );
        assertTrue(ex.getMessage().contains("own tweet"));
    }

    @Test
    @DisplayName("unlikeTweet: success")
    void unlikeTweet_success() {
        tweet.addLike(bob);
        when(tweetRepo.findById(10L)).thenReturn(Optional.of(tweet));
        when(userRepo.findByUsername("bob")).thenReturn(Optional.of(bob));

        svc.unliketweet(10L,"bob");
        assertFalse(tweet.getLikedByUsers().contains(bob));
    }

    @Test
    @DisplayName("addComment → maps to CommentDto")
    void addComment() {
        var cdto = new CommentCreationDto();
        cdto.setText("Nice!");
        when(tweetRepo.findById(10L)).thenReturn(Optional.of(tweet));
        when(userRepo.findByUsername("bob")).thenReturn(Optional.of(bob));

        Comment saved = new Comment("Nice!", bob, tweet);
        saved.setId(99L);
        when(commentRepo.save(any())).thenReturn(saved);

        CommentDto dto = svc.addComment(10L, cdto, "bob");
        assertEquals(99L, dto.getId());
        assertEquals("Nice!", dto.getText());
        assertEquals("bob", dto.getAuthorUsername());
    }

    @Test
    @DisplayName("deleteTweet: only author can delete")
    void deleteTweet_notAuthor() {
        when(tweetRepo.findById(10L)).thenReturn(Optional.of(tweet));
        when(userRepo.findByUsername("bob")).thenReturn(Optional.of(bob));

        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> svc.deleteTweet(10L,"bob")
        );
        assertTrue(ex.getMessage().contains("only delete your own"));
    }

    @Test
    @DisplayName("getTweetsByHashtag")
    void getTweetsByHashtag() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(alice));
        List<Tweet> list = List.of(tweet);
        Pageable pg = PageRequest.of(0,20);
        when(tweetRepo.findByHashtagName("test", pg)).thenReturn(list);

        var dtos = svc.getTweetsByHashtag("test","alice",0);
        assertEquals(1, dtos.size());
        assertEquals("alice", dtos.get(0).getAuthorUsername());
    }
}
