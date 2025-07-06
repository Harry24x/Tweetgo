package oth.ics.wtp.tweetgobackend.repositories;

import oth.ics.wtp.tweetgobackend.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findByUsernameContaining(String username);
}
