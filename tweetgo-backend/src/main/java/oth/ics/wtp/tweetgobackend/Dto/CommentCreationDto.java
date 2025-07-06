package oth.ics.wtp.tweetgobackend.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentCreationDto {
    @NotBlank(message = "Comment text cannot be blank")
    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String text;

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
} 