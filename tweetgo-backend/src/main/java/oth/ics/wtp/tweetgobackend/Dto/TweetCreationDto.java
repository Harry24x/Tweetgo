package oth.ics.wtp.tweetgobackend.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TweetCreationDto {
    @NotBlank(message = "Text cannot be blank")
    @Size(max = 280, message = "Text cannot exceed 280 characters")
    private String text;

    // Single no-arg constructor
    public TweetCreationDto() { }

    // Getter for text
    public String getText() {
        return text;
    }

    // Setter for text
    public void setText(String text) {
        this.text = text;
    }
}
