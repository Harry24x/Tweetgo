package oth.ics.wtp.tweetgobackend.controller;

import oth.ics.wtp.tweetgobackend.Dto.UserDto;
import oth.ics.wtp.tweetgobackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication auth) {
        String me = auth.getName();
        UserDto dto = userService.getUserProfile(me, me);
        return ResponseEntity.ok(dto);
    }
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateCurrentUser(
            @RequestBody UserDto update,
            Authentication auth
    ) {
        String me = auth.getName();
        UserDto updated = userService.updateProfile(me, update);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(
            @RequestParam("q") String query,
            Authentication auth
    ) {
        String me = auth != null ? auth.getName() : null;
        List<UserDto> users = userService.searchUsers(query, me);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{username}/follow")
    public ResponseEntity<?> followUser(
            @PathVariable String username,
            Authentication auth
    ) {
        String me = auth.getName();
        if (me.equals(username)) {
            return ResponseEntity
                    .badRequest()
                    .body("You cannot follow yourself.");
        }
        userService.followUser(me, username);
        return ResponseEntity.ok("Successfully followed " + username);
    }


    @PostMapping("/{username}/unfollow")
    public ResponseEntity<?> unfollowUser(
            @PathVariable String username,
            Authentication auth
    ) {
        String me = auth.getName();
        userService.unfollowUser(me, username);
        return ResponseEntity.ok("Successfully unfollowed " + username);
    }


    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserProfile(
            @PathVariable String username,
            Authentication auth
    ) {
        String me = auth.getName();
        UserDto dto = userService.getUserProfile(username, me);
        return ResponseEntity.ok(dto);
    }
}
