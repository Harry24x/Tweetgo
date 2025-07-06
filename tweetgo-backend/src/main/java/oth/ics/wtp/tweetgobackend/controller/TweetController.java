package oth.ics.wtp.tweetgobackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import oth.ics.wtp.tweetgobackend.Dto.TweetCreationDto;
import oth.ics.wtp.tweetgobackend.Dto.TweetDto;
import oth.ics.wtp.tweetgobackend.Dto.CommentDto;
import oth.ics.wtp.tweetgobackend.Dto.CommentCreationDto;
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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createTweet(
            @RequestBody TweetCreationDto dto,
            Authentication auth
    ) {
        try {
            String username = auth.getName();
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
    public ResponseEntity<List<TweetDto>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            Authentication auth) {
        List<TweetDto> feed = tweetService.getAggregatedTimeline(auth.getName(), page);
        return ResponseEntity.ok(feed);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<TweetDto>> getByUser(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            Authentication auth
    ) {
        String current = auth != null ? auth.getName() : null;
        List<TweetDto> tweets = tweetService.gettweetsByUsername(username, current, page);
        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/{tweetId}")
    public ResponseEntity<TweetDto> getTweetById(
            @PathVariable Long tweetId,
            Authentication auth
    ) {
        String currentUsername = auth != null ? auth.getName() : null;
        TweetDto tweet = tweetService.getTweetById(tweetId, currentUsername);
        return ResponseEntity.ok(tweet);
    }

    @DeleteMapping("/{tweetId}")
    public ResponseEntity<String> deleteTweet(
            @PathVariable Long tweetId,
            Authentication auth
    ) {
        String username = auth.getName();
        tweetService.deleteTweet(tweetId, username);
        return ResponseEntity.ok("Tweet deleted successfully.");
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

    @PostMapping("/{tweetId}/comments")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long tweetId,
            @RequestBody CommentCreationDto commentDto,
            Authentication auth
    ) {
        CommentDto comment = tweetService.addComment(tweetId, commentDto, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping("/{tweetId}/comments")
    public ResponseEntity<List<CommentDto>> getComments(
            @PathVariable Long tweetId,
            @RequestParam(defaultValue = "0") int page
    ) {
        List<CommentDto> comments = tweetService.getCommentsByTweetId(tweetId, page);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/hashtag/{hashtagName}")
    public ResponseEntity<List<TweetDto>> getTweetsByHashtag(
            @PathVariable String hashtagName,
            @RequestParam(defaultValue = "0") int page,
            Authentication auth
    ) {
        String currentUsername = auth != null ? auth.getName() : null;
        List<TweetDto> tweets = tweetService.getTweetsByHashtag(hashtagName, currentUsername, page);
        return ResponseEntity.ok(tweets);
    }
}
