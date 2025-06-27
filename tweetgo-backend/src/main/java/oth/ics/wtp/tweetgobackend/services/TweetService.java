package oth.ics.wtp.tweetgobackend.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import oth.ics.wtp.tweetgobackend.Dto.TweetCreationDto;
import oth.ics.wtp.tweetgobackend.Dto.TweetDto;
import oth.ics.wtp.tweetgobackend.Entities.Tweet;
import oth.ics.wtp.tweetgobackend.Entities.User;
import oth.ics.wtp.tweetgobackend.repositories.TweetRepository;
import oth.ics.wtp.tweetgobackend.repositories.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public TweetService(TweetRepository tweetRepository, UserRepository userRepository, FileStorageService fileStorageService) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
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
        return tweetRepository.save(newtweet);
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
        dto.setLikedByCurrentUser(currentUser != null && tweet.getLikedByUsers().contains(currentUser));

        return dto;
    }

    @Transactional(readOnly = true)
    public List<TweetDto> getAggregatedTimeline(String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        List<User> followedUsers = List.copyOf(currentUser.getFollowing());
        if (followedUsers.isEmpty()) {
            return Collections.emptyList();
        }


        Pageable pageable = PageRequest.of(0, 20);


        List<Tweet> timelinetweets = tweetRepository.findByAuthorInOrderByTimestampDesc(followedUsers, pageable);

        return timelinetweets.stream()
                .map(tweet -> maptweetToDto(tweet, currentUser))
                .collect(Collectors.toList());
    }
    @Transactional
    public void liketweet(Long tweetId, String username) {
        // 1. Find the tweet to be liked.
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new IllegalArgumentException("tweet not found with ID: " + tweetId));

        // 2. Find the user who is liking the tweet.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        // 3. Apply business rules from the specification (F-L1)
        if (tweet.getAuthor().equals(user)) {
            throw new IllegalArgumentException("You cannot like your own tweet.");
        }

        if (tweet.getLikedByUsers().contains(user)) {

            throw new IllegalArgumentException("You have already liked this tweet.");
        }
        tweet.addLike(user);

    }

    // (OPTIONAL: For extra feature)
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
    public List<TweetDto> gettweetsByUsername(String username, String currentUsername) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElse(null);

        Pageable pageable = PageRequest.of(0, 20);
        List<Tweet> tweets = tweetRepository.findByAuthorOrderByTimestampDesc(user, pageable);

        return tweets.stream()
                .map(t -> maptweetToDto(t, currentUser))
                .collect(Collectors.toList());

    }
}