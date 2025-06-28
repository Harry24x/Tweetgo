package oth.ics.wtp.tweetgobackend.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import oth.ics.wtp.tweetgobackend.config.SecurityConfig;
import oth.ics.wtp.tweetgobackend.controller.AuthController;
import oth.ics.wtp.tweetgobackend.controller.TweetController;
import oth.ics.wtp.tweetgobackend.controller.UserController;
import oth.ics.wtp.tweetgobackend.security.JwtUtil;
import oth.ics.wtp.tweetgobackend.services.TweetService;
import oth.ics.wtp.tweetgobackend.services.UserService;

// Import your controllers under test
@SuppressWarnings("ALL")
@Import({ TweetController.class, UserController.class, AuthController.class, SecurityConfig.class })
public abstract class ControllerTestBase {
    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    // Common mocks for any controller
    @MockBean protected TweetService tweetService;
    @MockBean protected UserService userService;
    @MockBean protected JwtUtil jwtUtil;
    @MockBean protected Authentication authentication;
}
