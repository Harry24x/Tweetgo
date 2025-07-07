package oth.ics.wtp.tweetgobackend.services;

import org.mockito.junit.jupiter.MockitoExtension;
import oth.ics.wtp.tweetgobackend.Entities.User;
import oth.ics.wtp.tweetgobackend.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.core.userdetails.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    @DisplayName("loadUserByUsername—when user exists, returns UserDetails")
    void loadUserByUsername_UserExists() {

        User dbUser = new User("alice", "s3cr3t");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(dbUser));

        UserDetails details = service.loadUserByUsername("alice");
        assertThat(details.getUsername()).isEqualTo("alice");
        assertThat(details.getPassword()).isEqualTo("s3cr3t");
        assertThat(details.getAuthorities())
                .extracting("authority")
                .containsExactly("USER");

        verify(userRepository).findByUsername("alice");
    }

    @Test
    @DisplayName("loadUserByUsername—when user missing, throws UsernameNotFoundException")
    void loadUserByUsername_UserMissing() {
        when(userRepository.findByUsername("bob"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("bob"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository).findByUsername("bob");
    }
}
