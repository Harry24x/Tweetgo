package oth.ics.wtp.tweetgobackend.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import oth.ics.wtp.tweetgobackend.Dto.UserDto;
import oth.ics.wtp.tweetgobackend.controller.UserController;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

class UserControllerTest extends ControllerTestBase {

    @BeforeEach
    void setup() {
        given(authentication.getName()).willReturn("alice");
    }

    @Test
    void testFollowUnfollow() {
        // follow
        willDoNothing().given(userService).followUser("alice", "bob");
        ResponseEntity<?> fResp = new UserController(userService)
                .followUser("bob", authentication);
        assertEquals(HttpStatus.OK, fResp.getStatusCode());

        // unfollow
        willDoNothing().given(userService).unfollowUser("alice", "bob");
        ResponseEntity<?> uResp = new UserController(userService)
                .unfollowUser("bob", authentication);
        assertEquals(HttpStatus.OK, uResp.getStatusCode());
    }

    @Test
    void testSearchUsers() {
        UserDto a = new UserDto(); a.setUsername("amy");
        UserDto b = new UserDto(); b.setUsername("ben");
        given(userService.searchUsers("a", "alice")).willReturn(List.of(a, b));

        List<UserDto> out = new UserController(userService)
                .searchUsers("a", authentication)
                .getBody();

        assertEquals(2, out.size());
        assertTrue(out.stream().anyMatch(u -> u.getUsername().equals("amy")));
    }

    @Test
    void testGetProfile() {
        UserDto dto = new UserDto();
        dto.setUsername("charlie");
        given(userService.getUserProfile("charlie", "alice")).willReturn(dto);

        UserDto out = new UserController(userService)
                .getUserProfile("charlie", authentication)
                .getBody();

        assertEquals("charlie", out.getUsername());
    }
}
