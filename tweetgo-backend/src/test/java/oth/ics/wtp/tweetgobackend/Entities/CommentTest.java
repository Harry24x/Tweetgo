package oth.ics.wtp.tweetgobackend.Entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CommentTest {

    @Test
    void testNoArgConstructorAndSetters() {
        Comment comment = new Comment();
        comment.setId(42L);
        comment.setText("Great post!");
        User author = new User("jane", "pwd");
        Tweet tweet = new Tweet("Hello", author);
        comment.setAuthor(author);
        comment.setTweet(tweet);
        comment.onCreate();

        assertEquals(42L, comment.getId());
        assertEquals("Great post!", comment.getText());
        assertSame(author, comment.getAuthor());
        assertSame(tweet, comment.getTweet());
        assertNotNull(comment.getTimestamp());
        assertTrue(comment.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testAllArgsConstructor() {
        User author = new User("john", "secret");
        Tweet tweet = new Tweet("Testing", author);
        Comment comment = new Comment("Nice!", author, tweet);

        assertNull(comment.getTimestamp());

        comment.onCreate();
        assertNotNull(comment.getTimestamp());

        assertEquals("Nice!", comment.getText());
        assertSame(author, comment.getAuthor());
        assertSame(tweet, comment.getTweet());
    }

    @Test
    void testEqualsAndHashCode() {
        Comment c1 = new Comment();
        c1.setId(1L);
        Comment c2 = new Comment();
        c2.setId(1L);
        Comment c3 = new Comment();
        c3.setId(2L);

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());

        assertNotEquals(c1, c3);
        assertNotEquals(c1.hashCode(), c3.hashCode());

        assertNotEquals(c1, null);
        assertNotEquals(c1, "some string");
    }

    @Test
    void testToStringContainsFields() {
        User author = new User("alice", "pw");
        Comment comment = new Comment("Wow!", author, null);
        comment.setId(99L);
        comment.onCreate();

        String str = comment.toString();
        assertTrue(str.contains("99"));
        assertTrue(str.contains("Wow!"));
        assertTrue(str.contains("alice"));
        assertTrue(str.contains("timestamp="));
    }
}
