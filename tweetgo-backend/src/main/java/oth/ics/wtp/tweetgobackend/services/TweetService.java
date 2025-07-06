package oth.ics.wtp.tweetgobackend.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import oth.ics.wtp.tweetgobackend.Dto.TweetCreationDto;
import oth.ics.wtp.tweetgobackend.Dto.TweetDto;
import oth.ics.wtp.tweetgobackend.Dto.CommentDto;
import oth.ics.wtp.tweetgobackend.Dto.CommentCreationDto;
import oth.ics.wtp.tweetgobackend.Entities.Tweet;
import oth.ics.wtp.tweetgobackend.Entities.User;
import oth.ics.wtp.tweetgobackend.Entities.Comment;
import oth.ics.wtp.tweetgobackend.Entities.Hashtag;
import oth.ics.wtp.tweetgobackend.repositories.TweetRepository;
import oth.ics.wtp.tweetgobackend.repositories.UserRepository;
import oth.ics.wtp.tweetgobackend.repositories.CommentRepository;
import oth.ics.wtp.tweetgobackend.repositories.HashtagRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final HashtagRepository hashtagRepository;
    private final FileStorageService fileStorageService;
    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#(\\w+)");

    @Autowired
    public TweetService(TweetRepository tweetRepository, UserRepository userRepository, 
                       CommentRepository commentRepository, HashtagRepository hashtagRepository,
                       FileStorageService fileStorageService) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.hashtagRepository = hashtagRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public Tweet createtweet(TweetCreationDto tweetDto, String authorUsername, MultipartFile imageFile) {
        User author = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new IllegalArgumentException("User with username " + authorUsername + " not found."));

        Tweet newtweet = new Tweet();
        newtweet.setText(tweetDto.getText());
        newtweet.setAuthor(author);
        
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.storeFile(imageFile);
            String imageUrl = "/uploads/" + fileName;
            newtweet.setImageUrl(imageUrl);
        }
        
        // Extract and add hashtags
        Set<String> hashtagNames = extractHashtags(tweetDto.getText());
        for (String hashtagName : hashtagNames) {
            Hashtag hashtag = hashtagRepository.findByName(hashtagName)
                    .orElseGet(() -> hashtagRepository.save(new Hashtag(hashtagName)));
            newtweet.addHashtag(hashtag);
        }
        
        return tweetRepository.save(newtweet);
    }
    
    private Set<String> extractHashtags(String text) {
        Matcher matcher = HASHTAG_PATTERN.matcher(text);
        return matcher.results()
                .map(result -> result.group(1).toLowerCase())
                .collect(Collectors.toSet());
    }
    
    public TweetDto maptweetToDto(Tweet tweet, User currentUser) {
        if (tweet == null) return null;

        TweetDto dto = new TweetDto();
        dto.setId(tweet.getId());
        dto.setText(tweet.getText());
        dto.setTimestamp(tweet.getTimestamp());
        dto.setImageUrl(tweet.getImageUrl());
        dto.setAuthorUsername(tweet.getAuthor().getUsername());
        dto.setLikesCount(tweet.getLikesCount());
        dto.setCommentsCount(tweet.getCommentsCount());
        dto.setLikedByCurrentUser(currentUser != null && tweet.getLikedByUsers().contains(currentUser));
        dto.setHashtags(tweet.getHashtags().stream()
                .map(Hashtag::getName)
                .collect(Collectors.toSet()));

        return dto;
    }

    @Transactional(readOnly = true)
    public List<TweetDto> getAggregatedTimeline(String username, int page) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        List<User> followedUsers = List.copyOf(currentUser.getFollowing());
        if (followedUsers.isEmpty()) {
            return Collections.emptyList();
        }

        Pageable pageable = PageRequest.of(page, 20);
        List<Tweet> timelinetweets = tweetRepository.findByAuthorInOrderByTimestampDesc(followedUsers, pageable);

        return timelinetweets.stream()
                .map(tweet -> maptweetToDto(tweet, currentUser))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void liketweet(Long tweetId, String username) {

        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new IllegalArgumentException("tweet not found with ID: " + tweetId));


        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        if (tweet.getAuthor().equals(user)) {
            throw new IllegalArgumentException("You cannot like your own tweet.");
        }

        if (tweet.getLikedByUsers().contains(user)) {
            throw new IllegalArgumentException("You have already liked this tweet.");
        }
        tweet.addLike(user);
    }


    @Transactional
    public void unliketweet(Long tweetId, String username) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new IllegalArgumentException("tweet not found with ID: " + tweetId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        if (!tweet.getLikedByUsers().contains(user)) {
            throw new IllegalArgumentException("You have not liked this tweet.");
        }

        tweet.removeLike(user);
    }

    @Transactional(readOnly = true)
    public List<TweetDto> gettweetsByUsername(String username, String currentUsername, int page) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElse(null);

        Pageable pageable = PageRequest.of(page, 20);
        List<Tweet> tweets = tweetRepository.findByAuthorOrderByTimestampDesc(user, pageable);

        return tweets.stream()
                .map(t -> maptweetToDto(t, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TweetDto getTweetById(Long tweetId, String currentUsername) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new IllegalArgumentException("Tweet not found with ID: " + tweetId));
        
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElse(null);
        
        return maptweetToDto(tweet, currentUser);
    }

    @Transactional
    public void deleteTweet(Long tweetId, String username) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new IllegalArgumentException("Tweet not found with ID: " + tweetId));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        if (!tweet.getAuthor().equals(user)) {
            throw new IllegalArgumentException("You can only delete your own tweets.");
        }
        
        tweetRepository.delete(tweet);
    }

    @Transactional
    public CommentDto addComment(Long tweetId, CommentCreationDto commentDto, String username) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new IllegalArgumentException("Tweet not found with ID: " + tweetId));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        Comment comment = new Comment(commentDto.getText(), user, tweet);
        Comment savedComment = commentRepository.save(comment);
        
        return mapCommentToDto(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByTweetId(Long tweetId, int page) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new IllegalArgumentException("Tweet not found with ID: " + tweetId));
        
        Pageable pageable = PageRequest.of(page, 20);
        List<Comment> comments = commentRepository.findByTweetOrderByTimestampDesc(tweet, pageable);
        
        return comments.stream()
                .map(this::mapCommentToDto)
                .collect(Collectors.toList());
    }

    private CommentDto mapCommentToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setTimestamp(comment.getTimestamp());
        dto.setAuthorUsername(comment.getAuthor().getUsername());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<TweetDto> getTweetsByHashtag(String hashtagName, String currentUsername, int page) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElse(null);
        
        Pageable pageable = PageRequest.of(page, 20);
        List<Tweet> tweets = tweetRepository.findByHashtagName(hashtagName, pageable);
        
        return tweets.stream()
                .map(tweet -> maptweetToDto(tweet, currentUser))
                .collect(Collectors.toList());
    }
}