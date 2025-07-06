package oth.ics.wtp.tweetgobackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtFilterTest {

    private JwtUtil jwtUtil;
    private UserDetailsService userDetailsService;
    private JwtFilter jwtFilter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JwtUtil.class);
        userDetailsService = mock(UserDetailsService.class);
        jwtFilter = new JwtFilter(jwtUtil, userDetailsService);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldNotFilter_forAuthEndpoints() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/auth/login");
        jwtFilter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtUtil, userDetailsService);
    }

    @Test
    void doFilterInternal_withoutAuthorizationHeader() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/other");
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "No Authorization header → no authentication should be set");
    }

    @Test
    void doFilterInternal_withValidToken_setsAuthentication() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/other");
        when(request.getHeader("Authorization")).thenReturn("Bearer good-token");
        when(jwtUtil.extractUsername("good-token")).thenReturn("alice");
        when(jwtUtil.validateToken("good-token")).thenReturn(true);

        UserDetails aliceDetails = mock(UserDetails.class);
        when(aliceDetails.getAuthorities()).thenReturn(Collections.emptyList());
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(aliceDetails);

        jwtFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth, "Should have populated an Authentication");
        assertTrue(auth instanceof UsernamePasswordAuthenticationToken);
        assertEquals(aliceDetails, auth.getPrincipal());
        assertTrue(auth.isAuthenticated());
    }

    @Test
    void doFilterInternal_withInvalidToken_doesNotAuthenticate() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/other");
        when(request.getHeader("Authorization")).thenReturn("Bearer bad-token");
        when(jwtUtil.extractUsername("bad-token")).thenReturn("bob");
        when(jwtUtil.validateToken("bad-token")).thenReturn(false);
    when(userDetailsService.loadUserByUsername("bob"))
                .thenReturn(mock(UserDetails.class));

        jwtFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "Invalid token → authentication must not be set");
    }

    @Test
    void shouldNotFilter_evenIfAuthorizationHeaderPresent() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/auth/refresh");
        when(request.getHeader("Authorization")).thenReturn("Bearer anything");

        jwtFilter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
    }
}
