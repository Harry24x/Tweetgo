package oth.ics.wtp.tweetgobackend.Dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class LoginRequest {
    @NotBlank @Size(min = 3, max = 50)
    public String username;

    @NotBlank @Size(min = 6, max = 100)
    public String password;
}
