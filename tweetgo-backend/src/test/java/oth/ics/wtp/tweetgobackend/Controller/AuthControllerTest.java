package oth.ics.wtp.tweetgobackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import oth.ics.wtp.tweetgobackend.Dto.RegisterRequest;
import oth.ics.wtp.tweetgobackend.Dto.LoginRequest;
import oth.ics.wtp.tweetgobackend.Entities.User;
import oth.ics.wtp.tweetgobackend.security.JwtUtil;
import oth.ics.wtp.tweetgobackend.services.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;

    @MockBean AuthenticationManager authManager;
    @MockBean UserService       userService;
    @MockBean JwtUtil           jwtUtil;

    @Test
    void register_Success() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("alice");
        req.setPassword("pwd123");

        Mockito.when(userService.usernameExists("alice")).thenReturn(false);
        User fakeUser = new User("alice", "encodedPwd");
        Mockito.when(userService.registerUser("alice", "pwd123"))
                .thenReturn(fakeUser);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));      // body is empty
    }


    @Test
    void login_Success() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("alice");
        req.setPassword("pwd123");

        Authentication auth =
                new UsernamePasswordAuthenticationToken("alice", "pwd123");
        Mockito.when(authManager.authenticate(
                new UsernamePasswordAuthenticationToken("alice", "pwd123")
        )).thenReturn(auth);
        Mockito.when(jwtUtil.generateToken("alice"))
                .thenReturn("jwt-token-abc");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                // login only returns { "token": "..." }
                .andExpect(jsonPath("$.token").value("jwt-token-abc"))

        ;
    }

    @Test
    void login_BadCredentials() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("bob");

        req.setPassword("wrongpwd");

        Mockito.when(authManager.authenticate(
                new UsernamePasswordAuthenticationToken("bob", "wrongpwd")
        )).thenThrow(new BadCredentialsException("Bad creds"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }
}
