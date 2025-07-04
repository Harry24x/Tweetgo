package oth.ics.wtp.tweetgobackend.controller;

import oth.ics.wtp.tweetgobackend.Dto.UserProfileDto;
import oth.ics.wtp.tweetgobackend.Entities.User;
import oth.ics.wtp.tweetgobackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import oth.ics.wtp.tweetgobackend.Dto.UserDto;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam("q") String query, Authentication authentication) {
        String currentUsername = authentication != null ? authentication.getName() : null;
        // We need a new service method that returns DTOs
        List<UserDto> users = userService.searchUsers(query, currentUsername);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{username}/follow")
    public ResponseEntity<?> followUser(@PathVariable String username, Authentication authentication) {
        String currentUsername = authentication.getName();
        if (currentUsername.equals(username)) {
            return ResponseEntity.badRequest().body("You cannot follow yourself.");
        }
        userService.followUser(currentUsername, username);
        return ResponseEntity.ok().body("Successfully followed " + username);
    }

    @PostMapping("/{username}/unfollow")
    public ResponseEntity<?> unfollowUser(@PathVariable String username, Authentication authentication) {
        String currentUsername = authentication.getName();
        userService.unfollowUser(currentUsername, username);
        return ResponseEntity.ok().body("Successfully unfollowed " + username);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserProfile(@PathVariable String username, Authentication authentication) {
        String currentUsername = authentication.getName();
        UserDto userProfile = userService.getUserProfile(username, currentUsername);
        return ResponseEntity.ok(userProfile);
    }
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getMyProfile(Authentication auth) {
        String me = auth.getName();
        User user = userService.findByUsername(me);
        UserProfileDto dto = userService.toProfileDto(user);
        return ResponseEntity.ok(dto);
    }
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(
            @RequestBody UserProfileDto profile,
            Authentication auth
    ) {
        String me = auth.getName();   userService.updateProfile(me, profile.fullName, profile.email, profile.bio);

        return ResponseEntity.noContent().build();}
}
