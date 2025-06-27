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


}//o	I also began defining Data Transfer Objects (DTOs) like UserRegistrationDto,
// UserLoginDto, and PostCreationDto to handle data transfer between layers