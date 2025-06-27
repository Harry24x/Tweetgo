package oth.ics.wtp.tweetgobackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import oth.ics.wtp.tweetgobackend.Dto.TweetCreationDto;
import oth.ics.wtp.tweetgobackend.Dto.TweetDto;
import oth.ics.wtp.tweetgobackend.services.TweetService;
import oth.ics.wtp.tweetgobackend.Entities.Tweet;

import java.util.List;

@RestController
@RequestMapping("/api/tweet")
public class TweetController {

    private final TweetService tweetService;

    @Autowired
    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    // ← Add this annotation so the endpoint is exposed
    @PostMapping
    public ResponseEntity<?> createTweet(
            @RequestPart("tweetData") TweetCreationDto tweetDto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            Authentication authentication) {

        String username = authentication.getName();
        try {
            Tweet newTweet = tweetService.createtweet(tweetDto, username, imageFile);
            TweetDto responseDto = tweetService.maptweetToDto(newTweet, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Error creating tweet: " + e.getMessage());
        }
    }

    @GetMapping("/feed")
    public List<TweetDto> getTimelineFeed(Authentication authentication) {
        return tweetService.getAggregatedTimeline(authentication.getName());
    }

    @GetMapping("/user/{username}")
    public List<TweetDto> getTweetsByUsername(
            @PathVariable String username,
            Authentication authentication) {

        String currentUser = authentication != null ? authentication.getName() : null;
        return tweetService.gettweetsByUsername(username, currentUser);
    }

    @PostMapping("/{tweetId}/like")
    public ResponseEntity<String> likeTweet(
            @PathVariable Long tweetId,
            Authentication authentication) {

        tweetService.liketweet(tweetId, authentication.getName());
        return ResponseEntity.ok("Tweet liked successfully.");
    }

    @DeleteMapping("/{tweetId}/unlike")
    public ResponseEntity<String> unlikeTweet(
            @PathVariable Long tweetId,
            Authentication authentication) {

        tweetService.unliketweet(tweetId, authentication.getName());
        return ResponseEntity.ok("Tweet unliked successfully.");
    }
}
