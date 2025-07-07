package oth.ics.wtp.tweetgobackend.Entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class HashtagTest {
    private Hashtag hashtag;
    private Tweet tweetA, tweetB;

    @BeforeEach
    void setUp() {
        hashtag = new Hashtag("java");

        User author = new User("foo", "bar");
        author.setId(1L);

        tweetA = new Tweet("Tweet A", author);
        tweetA.setId(1L);
        tweetA.onCreate();

        tweetB = new Tweet("Tweet B", author);
        tweetB.setId(2L);
        tweetB.onCreate();
    }

    @Test
    void testNoArgConstructorAndNameSetter() {
        Hashtag h = new Hashtag();
        h.setName("hello");
        assertEquals("hello", h.getName());
    }

    @Test
    void testAllArgsConstructor() {
        Hashtag h = new Hashtag("spring");
        assertEquals("spring", h.getName());
        assertTrue(h.getTweets().isEmpty());
    }

    @Test
    void testToStringIncludesIdAndName() throws Exception {
        // set private id via reflection
        Field idField = Hashtag.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(hashtag, 5L);

        String s = hashtag.toString();
        assertTrue(s.contains("5"),   "toString should include the id");
        assertTrue(s.contains("java"), "toString should include the name");
    }

    @Test
    void testAddTweetAndBiDirectionalLink() {
        hashtag.addTweet(tweetA);
        assertTrue(hashtag.getTweets().contains(tweetA), "should contain tweetA");
        assertTrue(tweetA.getHashtags().contains(hashtag), "tweetA should know about the hashtag");
    }

    @Test
    void testRemoveTweetAndBiDirectionalUnlink() {

        hashtag.addTweet(tweetA);
        hashtag.addTweet(tweetB);
        assertEquals(2, hashtag.getTweets().size(), "both tweets should have been added");
        hashtag.removeTweet(tweetA);
        assertFalse(hashtag.getTweets().contains(tweetA), "hashtag should no longer contain tweetA");
        assertFalse(tweetA.getHashtags().contains(hashtag), "tweetA should no longer reference the hashtag");
        hashtag.removeTweet(tweetA);
        assertEquals(1, hashtag.getTweets().size(), "removing non-existent link should not change size");
    }

    @Test
    void testEqualsAndHashCodeBasedOnName() {
        Hashtag h1 = new Hashtag("test");
        Hashtag h2 = new Hashtag("test");
        Hashtag h3 = new Hashtag("other");

        assertEquals(h1, h2,   "equal names should be equal");
        assertEquals(h1.hashCode(), h2.hashCode(), "hashCode must match for equal objects");

        assertNotEquals(h1, h3, "different names should not be equal");
    }
}
