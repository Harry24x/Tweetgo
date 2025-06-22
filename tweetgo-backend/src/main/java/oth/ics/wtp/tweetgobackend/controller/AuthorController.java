package oth.ics.wtp.tweetgobackend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oth.ics.wtp.tweetgobackend.dtos.AuthorResponse;
import oth.ics.wtp.tweetgobackend.dtos.LoginRequest;
import oth.ics.wtp.tweetgobackend.dtos.RegisterRequest;
import oth.ics.wtp.tweetgobackend.security.JwtUtil;
import oth.ics.wtp.tweetgobackend.services.UserService;

@RestController
@RequestMapping("/auth")
public class AuthorController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userService.usernameExists(request.username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        }
        userService.registerUser(request.username, request.password);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthorResponse> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username, request.password));
        String token = jwtUtil.generateToken(request.username);
        return ResponseEntity.ok(new AuthorResponse(token));
    }
}
