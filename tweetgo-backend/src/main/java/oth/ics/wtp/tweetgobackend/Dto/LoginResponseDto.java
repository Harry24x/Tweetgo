package oth.ics.wtp.tweetgobackend.Dto;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class LoginResponseDto {

        private String token;
        private String tokenType = "Bearer";


}