package oth.ics.wtp.tweetgobackend.services;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oth.ics.wtp.tweetgobackend.Dto.UserDto;
import oth.ics.wtp.tweetgobackend.Entities.User;
import oth.ics.wtp.tweetgobackend.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Getter
@Setter

public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public User registerUser(String username, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, encodedPassword);
        return userRepository.save(user);
    }

    @Transactional
    public void followUser(String followerUsername, String targetUsername) {
        User follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + followerUsername));
        User target = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + targetUsername));
        
        if (follower.equals(target)) {
            throw new IllegalArgumentException("You cannot follow yourself.");
        }
        
        follower.followUser(target);
        userRepository.save(follower);
        userRepository.save(target);
    }

    @Transactional
    public void unfollowUser(String followerUsername, String targetUsername) {
        User follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + followerUsername));
        User target = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + targetUsername));
        
        follower.unfollowUser(target);
        userRepository.save(follower);
        userRepository.save(target);
    }

    @Transactional(readOnly = true)
    public List<UserDto> searchUsers(String query, String currentUsername) {
        return userRepository.findByUsernameContaining(query).stream()
                .filter(u -> !u.getUsername().equals(currentUsername))
                .map(user -> mapUserToDto(user, currentUsername))
                .collect(Collectors.toList());
    }

    private UserDto mapUserToDto(User user) {
        return mapUserToDto(user, null);
    }

    @Transactional(readOnly = true)
    public UserDto getUserProfile(String username, String currentUsername) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return mapUserToDto(user, currentUsername);
    }
    @Transactional
    public UserDto updateProfile(String username, UserDto update) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));


        if (update.getPassword() != null && !update.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(update.getPassword()));
        }

        userRepository.save(user);
        return mapUserToDto(user, username);
    }


    private UserDto mapUserToDto(User user, String currentUsername) {
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setFollowersCount(user.getFollowers().size());
        dto.setFollowingCount(user.getFollowing().size());
        
        if (currentUsername != null && !currentUsername.equals(user.getUsername())) {
            User currentUser = userRepository.findByUsername(currentUsername).orElse(null);
            if (currentUser != null) {
                dto.setFollowedByCurrentUser(currentUser.getFollowing().contains(user));
            }
        }
        
        return dto;
    }
}