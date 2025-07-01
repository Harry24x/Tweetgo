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
@CrossOrigin(origins = "http://localhost:5173")
public class TweetController {

    private final TweetService tweetService;

    @Autowired
    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    // ← Add this annotation so the endpoint is exposed
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createTweet(
            @RequestBody TweetCreationDto dto,

            Authentication auth
    ) {
        try {
            String username = auth.getName();
            // pass `null` for imageFile until you build an upload UI
            Tweet saved = tweetService.createtweet(dto, username, null);
            TweetDto out = tweetService.maptweetToDto(saved, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(out);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Error creating tweet: " + e.getMessage());
        }
    }


    @GetMapping("/feed")
    public ResponseEntity<List<TweetDto>> getFeed(Authentication auth) {
        List<TweetDto> feed = tweetService.getAggregatedTimeline(auth.getName());
        return ResponseEntity.ok(feed);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<TweetDto>> getByUser(
            @PathVariable String username,
            Authentication auth
    ) {
        String current = auth != null ? auth.getName() : null;
        List<TweetDto> tweets = tweetService.gettweetsByUsername(username, current);
        return ResponseEntity.ok(tweets);
    }

    @PostMapping("/{tweetId}/like")
    public ResponseEntity<String> likeTweet(
            @PathVariable Long tweetId,
            Authentication auth) {

        tweetService.liketweet(tweetId, auth.getName());
        return ResponseEntity.ok("Tweet liked successfully.");
    }

    @DeleteMapping("/{tweetId}/unlike")
    public ResponseEntity<String> unlikeTweet(
            @PathVariable Long tweetId,
            Authentication auth) {

        tweetService.unliketweet(tweetId, auth.getName());
        return ResponseEntity.ok("Tweet unliked successfully.");
    }
}
