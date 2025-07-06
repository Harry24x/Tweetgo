package oth.ics.wtp.tweetgobackend.Dto;

public class UserDto {
    private String username;
    private String password;
    private int followersCount;
    private int followingCount;
    private boolean followedByCurrentUser; // optional

    public UserDto() {}

    public String getPassword() {
        return password;
    }
    public void setPassword(String username) {
        this.password = password;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    // followersCount
    public int getFollowersCount() {
        return followersCount;
    }
    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    // followingCount
    public int getFollowingCount() {
        return followingCount;
    }
    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    // followedByCurrentUser (if used)
    public boolean isFollowedByCurrentUser() {
        return followedByCurrentUser;
    }
    public void setFollowedByCurrentUser(boolean followedByCurrentUser) {
        this.followedByCurrentUser = followedByCurrentUser;
    }
}

