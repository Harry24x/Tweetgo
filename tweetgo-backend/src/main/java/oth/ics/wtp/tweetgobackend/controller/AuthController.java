package oth.ics.wtp.tweetgobackend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import oth.ics.wtp.tweetgobackend.Dto.AuthorResponse;
import oth.ics.wtp.tweetgobackend.Dto.LoginRequest;
import oth.ics.wtp.tweetgobackend.Dto.RegisterRequest;
import oth.ics.wtp.tweetgobackend.security.JwtUtil;
import oth.ics.wtp.tweetgobackend.services.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = { "http://localhost" , "http://localhost:5173", "http://localhost:80" })
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> Register(@Valid @RequestBody RegisterRequest request) {
        if (userService.usernameExists(request.username)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        }
        userService.registerUser(request.username, request.password);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username, request.password));
            String token = jwtUtil.generateToken(request.username);
            return ResponseEntity.ok(new AuthorResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }
}
