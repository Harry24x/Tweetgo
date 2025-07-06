package oth.ics.wtp.tweetgobackend.Entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TweetTest {

    private Tweet tweet;
    private User author;

    @BeforeEach
    void setUp() {
        author = new User("testuser", "password123");
        tweet = new Tweet("Hello World!", author);
    }

    @Test
    void testInitialState() {
        assertEquals("Hello World!", tweet.getText());
        assertEquals(author, tweet.getAuthor());


        assertEquals(0, tweet.getLikesCount());
        assertTrue(tweet.getLikedByUsers().isEmpty());
        assertEquals(0, tweet.getCommentsCount());
        assertTrue(tweet.getComments().isEmpty());
        assertTrue(tweet.getHashtags().isEmpty());
        assertNull(tweet.getTimestamp());
    }

    @Test
    void testTimestampOnCreate() {
        tweet.onCreate();
        assertNotNull(tweet.getTimestamp());
        assertTrue(tweet.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testLikeAndRemoveLike() {
        User liker = new User("liker", "pw");
        tweet.addLike(liker);
        assertEquals(1, tweet.getLikesCount());
        assertTrue(tweet.getLikedByUsers().contains(liker));
        tweet.removeLike(liker);
        assertEquals(0, tweet.getLikesCount());
        assertFalse(tweet.getLikedByUsers().contains(liker));
    }

    @Test
    void testCommentAddAndRemove() {
        Comment comment = new Comment();
        tweet.addComment(comment);

        assertEquals(1, tweet.getCommentsCount());
        assertTrue(tweet.getComments().contains(comment));
        assertEquals(tweet, comment.getTweet());

        tweet.removeComment(comment);
        assertEquals(0, tweet.getCommentsCount());
        assertFalse(tweet.getComments().contains(comment));
        assertNull(comment.getTweet());
    }

    @Test
    void testHashtagAddAndRemove() {
        Hashtag tag = new Hashtag();
        tweet.addHashtag(tag);

        assertTrue(tweet.getHashtags().contains(tag));
        assertTrue(tag.getTweets().contains(tweet));

        tweet.removeHashtag(tag);
        assertFalse(tweet.getHashtags().contains(tag));
        assertFalse(tag.getTweets().contains(tweet));
    }

    @Test
    void testEqualsAndHashCodeById() {
        Tweet t1 = new Tweet("A", author);
        Tweet t2 = new Tweet("B", author);
        Tweet t3 = new Tweet("C", author);

        t1.setId(1L);
        t2.setId(1L);
        t3.setId(2L);

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());

        assertNotEquals(t1, t3);
    }

    @Test
    void testImageUrl() {
        tweet.setImageUrl("http://img");
        assertEquals("http://img", tweet.getImageUrl());
    }

    @Test
    void testToStringIncludesAllFields() {
        tweet.setId(42L);
        tweet.onCreate(); // set timestamp
        tweet.addLike(new User("u1","p"));
        tweet.addComment(new Comment());

        String s = tweet.toString();
        assertTrue(s.contains("id=42"));
        assertTrue(s.contains("text='Hello World!'"));
        assertTrue(s.contains("author=testuser"));
        assertTrue(s.contains("likesCount=1"));
        assertTrue(s.contains("commentsCount=1"));
        assertTrue(s.contains("timestamp="));
    }
}
