package oth.ics.wtp.tweetgobackend.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String SECRET = "01234567890123456789012345678901";
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {

        jwtUtil = new JwtUtil(SECRET, 60_000L);
    }

    @Test
    void generateToken_shouldReturnValidJwt() {
        String token = jwtUtil.generateToken("alice");
        assertNotNull(token, "Token should not be null");
        assertTrue(jwtUtil.validateToken(token), "Generated token must validate");
        assertEquals("alice", jwtUtil.extractUsername(token), "Username must round-trip");
    }

    @Test
    void validateToken_tamperedSignatureShouldFail() {
        String token = jwtUtil.generateToken("bob");
        char last = token.charAt(token.length() - 1);
        String tampered = token.substring(0, token.length() - 1)
                + (last == 'a' ? 'b' : 'a');
        assertFalse(jwtUtil.validateToken(tampered), "Tampered token should not validate");
    }

    @Test
    void validateToken_withWrongSecretShouldFail() {
        String token = jwtUtil.generateToken("carol");
        JwtUtil other = new JwtUtil("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", 60_000L);
        assertFalse(other.validateToken(token), "Token signed with a different secret must not validate");
    }

    @Test
    void validateToken_expiredTokenShouldFail() throws InterruptedException {
        JwtUtil shortLived = new JwtUtil(SECRET, 10L);
        String token = shortLived.generateToken("dan");
        Thread.sleep(50);
        assertFalse(shortLived.validateToken(token), "Expired token should not validate");
    }

    @Test
    void extractUsername_invalidTokenShouldThrow() {
        assertThrows(JwtException.class,
                () -> jwtUtil.extractUsername("this.is.not.a.jwt"),
                "extractUsername on an invalid token should throw");
    }

    @Test
    void validateToken_nullOrEmptyShouldFail() {
        assertFalse(jwtUtil.validateToken(""), "Empty token should not validate");
        assertFalse(jwtUtil.validateToken(null), "Null token should not validate");
    }
}
